/* scala-stm - (c) 2010, LAMP/EPFL */

package scala.concurrent.stm
package skel

import annotation.tailrec


class CallbackList[A] private (private var _size: Int,
                               private var _data: Array[A => Unit]) {
  def this() = this(0, new Array[A => Unit](InitialCapacity))

  private def InitialCapacity = 16
  private def MaxEmptyCapacity = 8192

  def isEmpty: Boolean = _size == 0

  def size: Int = _size

  def size_=(newSize: Int) {
    if (newSize < 0 || newSize > _size)
      throw new IllegalArgumentException

    if (newSize == 0 && _data.length > MaxEmptyCapacity) {
      // reallocate if the array is too big, so that a single large txn doesn't
      // permanently increase the memory footprint of this thread
      reset()
    } else {
      java.util.Arrays.fill(_data, newSize, _size, null)
      _size = newSize
    }
  }

  private def reset() {
    _data = new Array[A => Unit](InitialCapacity)
    _size = 0
  }

  def += (handler: A => Unit) {
    if (_size == _data.length)
      grow
    _data(_size) = handler
    _size += 1
  }

  private def grow() {
    val a = new Array[A => Unit](_data.length * 2)
    System.arraycopy(_data, 0, a, 0, _data.length)
    _data = a
  }

  def apply(i: Int): (A => Unit) = _data(i)

  def fire(level: NestingLevel, arg: A): Boolean = fire(level, arg, 0)

  @tailrec private def fire(level: NestingLevel, arg: A, i: Int): Boolean = {
    if (!shouldFire(level))
      false
    else if (i >= _size)
      true
    else {
      try {
        _data(i)(arg)
      } catch {
        case x => level.requestRollback(Txn.UncaughtExceptionCause(x))
      }
      fire(level, arg, i + 1)
    }
  }

  private def shouldFire(level: NestingLevel): Boolean = !level.status.isInstanceOf[Txn.RolledBack]

  /** Sets the size of this callback list to `newSize`, and returns a the
   *  discarded handlers.
   */
  def truncate(newSize: Int): Seq[A => Unit] = {
    if (_size == 0) {
      // nothing to do
      Seq.empty[A => Unit]
    } else if (newSize == 0) {
      // hand off the array, allocate a new one for us
      val z = _data.slice(0, _size)
      reset()
      z
    } else {
      // copy
      val z = new Array[A => Unit](_size - newSize)
      System.arraycopy(_data, newSize, z, 0, z.length)
      size = newSize
      z
    }
  }
}