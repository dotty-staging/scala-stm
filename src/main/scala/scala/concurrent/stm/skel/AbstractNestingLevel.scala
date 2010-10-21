/* scala-stm - (c) 2010, LAMP/EPFL */

package scala.concurrent.stm
package skel

trait AbstractNestingLevel extends NestingLevel {
  import Txn._

  def txn: InTxn
  def par: AbstractNestingLevel

  def parent: Option[NestingLevel] = Option(par)

  private var _beforeCommit: SuccessCallbackList[InTxn] = null
  private var _whileValidating: SuccessCallbackList[InTxn] = null
  private var _whilePreparing: SuccessCallbackList[InTxnEnd] = null
  private var _whileCommitting: SuccessCallbackList[InTxnEnd] = null
  private var _afterCommit: SuccessCallbackList[Txn.Status] = null
  private var _afterRollback: FailureCallbackList[Txn.Status] = null

  def hasBeforeCommit = _beforeCommit != null && !_beforeCommit.isEmpty

  def beforeCommit: SuccessCallbackList[InTxn] = {
    if (_beforeCommit == null)
      _beforeCommit = new SuccessCallbackList[InTxn](this)
    _beforeCommit
  }

  def hasWhileValidating = _whileValidating != null && !_whileValidating.isEmpty

  def whileValidating: SuccessCallbackList[InTxn] = {
    if (_whileValidating == null)
      _whileValidating = new SuccessCallbackList[InTxn](this)
    _whileValidating
  }

  def hasWhilePreparing = _whilePreparing != null && !_whilePreparing.isEmpty

  def whilePreparing: SuccessCallbackList[InTxnEnd] = {
    if (_whilePreparing == null)
      _whilePreparing = new SuccessCallbackList[InTxnEnd](this)
    _whilePreparing
  }

  def hasWhileCommitting = _whileCommitting != null && !_whileCommitting.isEmpty

  def whileCommitting: SuccessCallbackList[InTxnEnd] = {
    if (_whileCommitting == null)
      _whileCommitting = new SuccessCallbackList[InTxnEnd](this)
    _whileCommitting
  }

  def hasAfterCommit = _afterCommit != null && !_afterCommit.isEmpty

  def afterCommit: SuccessCallbackList[Status] = {
    if (_afterCommit == null)
      _afterCommit = new SuccessCallbackList[Status](this)
    _afterCommit
  }

  def hasAfterRollback = _afterRollback != null && !_afterRollback.isEmpty

  def afterRollback: FailureCallbackList[Status] = {
    if (_afterRollback == null)
      _afterRollback = new FailureCallbackList[Status](this)
    _afterRollback
  }


  def mergeIntoParent() {
    if (hasBeforeCommit)
      par.beforeCommit ++= beforeCommit
    if (hasWhileValidating)
      par.whileValidating ++= whileValidating
    if (hasWhilePreparing)
      par.whilePreparing ++= whilePreparing
    if (hasWhileCommitting)
      par.whileCommitting ++= whileCommitting
    if (hasAfterCommit)
      par.afterCommit ++= afterCommit
    if (hasAfterRollback)
      par.afterRollback ++= afterRollback
  }
}
