/* scala-stm - (c) 2009-2011, Stanford University, PPL */

package scala.concurrent.stm
package skel

import scala.collection.mutable

private[stm] object HashTrieTMap {

  def empty[A, B]: TMap[A, B] = new HashTrieTMap(Ref(TxnHashTrie.emptyMapNode[A, B]).single)

  def newBuilder[A, B]: mutable.Builder[(A, B), TMap[A, B]] = new mutable.Builder[(A, B), TMap[A, B]] {
    var root: TxnHashTrie.BuildingNode[A, B] = TxnHashTrie.emptyMapBuildingNode[A, B]

    def clear(): Unit = { root = TxnHashTrie.emptyMapBuildingNode[A, B] }

    def addOne(elem: (A, B)): this.type = { root = TxnHashTrie.buildingPut(root, elem._1, elem._2) ; this }

    def result(): TMap[A, B] = {
      val r = root
      root = null
      new HashTrieTMap(Ref(r.endBuild).single)
    }
  }
}

private[skel] class HashTrieTMap[A, B] private (root0: Ref.View[TxnHashTrie.Node[A, B]])
  extends TxnHashTrie[A, B](root0)
    with TMapViaClone[A, B] {

  //// construction

  override def empty: TMap.View[A, B] = new HashTrieTMap(Ref(TxnHashTrie.emptyMapNode[A, B]).single)
  override def clone: HashTrieTMap[A, B] = new HashTrieTMap(cloneRoot)

  //// TMap.View aggregates

  override def isEmpty: Boolean = singleIsEmpty
  override def size: Int = singleSize
  override def iterator: Iterator[(A, B)] = mapIterator
  override def keysIterator: Iterator[A] = mapKeyIterator
  override def valuesIterator: Iterator[B] = mapValueIterator
  override def foreach[U](f: ((A, B)) => U): Unit = singleMapForeach(f)

  override def clear(): Unit = { root() = TxnHashTrie.emptyMapNode[A, B] }

  //// TMap.View per-element

  override def contains(key: A): Boolean = singleContains(key)
  override def apply(key: A): B = singleGetOrThrow(key)
  def get(key: A): Option[B] = singleGet(key)

  override def put(key: A, value: B): Option[B] = singlePut(key, value)
  override def update(key: A, value: B): Unit = singlePut(key, value)
  def addOne(elem: (A, B)): this.type = { singlePut(elem._1, elem._2) ; this }

  override def remove(key: A): Option[B] = singleRemove(key)
  override def subtractOne(key: A): this.type = { singleRemove(key) ; this }

  //// optimized TMap versions

  def isEmpty(implicit txn: InTxn): Boolean = txnIsEmpty
  def size(implicit txn: InTxn): Int = singleSize
  def foreach[U](f: ((A, B)) => U)(implicit txn: InTxn): Unit = txnMapForeach(f)

  def contains(key: A)(implicit txn: InTxn): Boolean = txnContains(key)
  def apply(key: A)(implicit txn: InTxn): B = txnGetOrThrow(key)
  def get(key: A)(implicit txn: InTxn): Option[B] = txnGet(key)
  def put(key: A, value: B)(implicit txn: InTxn): Option[B] = txnPut(key, value)
  def remove(key: A)(implicit txn: InTxn): Option[B] = txnRemove(key)

  def mapValuesInPlace(f: (A, B) => B)(implicit txn: InTxn): this.type = {
    single.mapValuesInPlace { case (a, b) => f(a, b) }
    this
  }
  def filterInPlace(p: (A, B) => Boolean)(implicit txn: InTxn): this.type = {
    single.filterInPlace(p)
    this
  }
}
