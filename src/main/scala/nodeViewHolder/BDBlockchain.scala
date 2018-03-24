package nodeViewHolder

import blocks.BDBlock
import scorex.core.{ModifierId, consensus}
import scorex.core.consensus.BlockChain.Score
import scorex.core.consensus.{BlockChain, History, ModifierSemanticValidity}
import transaction.{BlockchainDevelopersTransaction, Sha256PreimageProposition}

import scala.util.Try

class BDBlockchain(blockchain: Seq[BDBlock] = Seq()) extends BlockChain[Sha256PreimageProposition, BlockchainDevelopersTransaction, BDBlock, DBSyncInfo,
  BDBlockchain] {
  override def height(): Int = ???

  override def heightOf(blockId: ModifierId): Option[Int] = ???

  override def blockAt(height: Int): Option[BDBlock] = ???

  override def children(blockId: ModifierId): Seq[BDBlock] = ???

  override def score(block: BDBlock): Score = ???

  override def chainScore(): Score = ???

  override def append(modifier: BDBlock): Try[(BDBlockchain, History.ProgressInfo[BDBlock])] = {
    //TODO: add to 'toApply'
    Try(new BDBlockchain(blockchain ++ List(modifier)), new consensus.History.ProgressInfo[BDBlock]() {})
  }

  override def reportSemanticValidity(modifier: BDBlock, valid: Boolean, lastApplied: ModifierId): (BDBlockchain, History.ProgressInfo[BDBlock]) = ???

  override def modifierById(modifierId: ModifierId): Option[BDBlock] = ???

  override def isSemanticallyValid(modifierId: ModifierId): ModifierSemanticValidity.Value = ???

  override def syncInfo: DBSyncInfo = ???

  override def compare(other: DBSyncInfo): History.HistoryComparisonResult.Value = ???

  override type NVCT = BDBlockchain


}
