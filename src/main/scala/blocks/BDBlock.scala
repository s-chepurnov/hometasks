package blocks

import io.circe.Json
import org.msgpack.core.MessagePack
import scorex.core.block.Block
import scorex.core.block.Block.Version
import scorex.core.serialization.Serializer
import scorex.core.{ModifierId, ModifierTypeId}
import scorex.crypto.hash.{Digest32, Sha256}
import transaction.{BCTransactionSerializer, BlockchainDevelopersTransaction, Sha256PreimageProposition}

import scala.util.Try

case class BDBlock(transactions: Seq[BlockchainDevelopersTransaction],
                   parentId: ModifierId,
                   currentTarget: BigInt,
                   nonce: Long,
                   version: Version,
                   timestamp: Long) extends Block[Sha256PreimageProposition, BlockchainDevelopersTransaction] {
  override type M = BDBlock

  override val modifierTypeId: ModifierTypeId = ModifierTypeId @@ 2.toByte

  val hash: Digest32 = {
    Sha256(serializer.toBytes(this))
  }

  override val id: ModifierId = ModifierId @@ (Digest32 untag hash)

  override def json: Json = ???

  override def serializer: Serializer[BDBlock] = BDBlockSerializer
}

object BDBlockSerializer extends Serializer[BDBlock] {

  override def toBytes(obj: BDBlock): Array[Version] = {
    val packer = MessagePack.newDefaultBufferPacker()

    packer.packBinaryHeader(obj.transactions.size)
    for {
      tx <- obj.transactions
    } yield {
      packer.packBinaryHeader(tx.serializer.toBytes(tx).length)
      packer.writePayload(tx.serializer.toBytes(tx))
    }

    packer.packBinaryHeader(obj.parentId.length)
    packer.writePayload(obj.parentId)

    packer.packBigInteger(obj.currentTarget.bigInteger)
    packer.packLong(obj.nonce)
    packer.packByte(obj.version)
    packer.packLong(obj.timestamp)
    packer.toByteArray
  }

  override def parseBytes(bytes: Array[Version]): Try[BDBlock] = {
    val unpacker = MessagePack.newDefaultUnpacker(bytes)
    val numTxs = unpacker.unpackArrayHeader()

    val transactions = for {
      i <- Range(0, numTxs)
    } yield {
      val len = unpacker.unpackBinaryHeader()
      BCTransactionSerializer.parseBytes(unpacker.readPayload(len)).getOrElse()
    }

    val parentIdLen = unpacker.unpackArrayHeader()
    val parentId: ModifierId = ModifierId @@ unpacker.readPayload(parentIdLen)

    val currentTarget: BigInt = BigInt(unpacker.unpackBigInteger())

    val nonce = unpacker.unpackLong()
    val version = unpacker.unpackByte()
    val timestamp = unpacker.unpackLong()

    Try(BDBlock(transactions.asInstanceOf[Seq[BlockchainDevelopersTransaction]], parentId, currentTarget, nonce, version, timestamp))
  }

}

