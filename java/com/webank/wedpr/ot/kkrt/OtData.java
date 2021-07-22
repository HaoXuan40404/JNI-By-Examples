package com.webank.wedpr.ot.kkrt;

public class OtData {
    long[] senderSeed;
    byte[] senderSeedHash;
    byte[] senderPackSeed;
    long[][] enMessage;
    byte[][] hash;
    long[] senderMatrix;

    long[] receiverSeed;
    long[] receiverMatrix;
    byte[] receiverPack;
}
