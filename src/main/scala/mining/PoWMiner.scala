package mining

import scorex.crypto.hash.CryptographicHash32

import scala.math.BigInt

class PoWMiner[HF <: CryptographicHash32](hashFunction: HF) {

  private val MaxTarget: BigInt = BigInt(1, Array.fill(32)((-1).toByte))

  def doWork(data: Array[Byte], difficulty: BigInt): ProvedData = {
    var nonce = 0
    var provedData: ProvedData = new ProvedData(data , nonce)

    while ( !validateWork(provedData, difficulty)) {
      nonce = nonce + 1
      provedData = new ProvedData(data , nonce)
    }

    provedData
  }

  def validateWork(data: ProvedData, difficulty: BigInt): Boolean =
    BigInt(1, hashFunction.hash(data.bytes)) <= (MaxTarget / difficulty)

}
