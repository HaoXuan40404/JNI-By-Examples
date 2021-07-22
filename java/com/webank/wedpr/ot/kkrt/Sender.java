package com.webank.wedpr.ot.kkrt;

import java.util.HashMap;

public class Sender {
    long senderHandle;
    int choiceCount;
    int messageCount;

    // this
    public void init(int choiceCount, int messageCount, HashMap<Long, String> data) {
        this.choiceCount = choiceCount;
        this.messageCount = messageCount;
        senderHandle = NativeInterface.initSender(choiceCount, messageCount, data);
    }

    // Encrypted
    private OtData getSenderEncryptedMessage() {
        OtData otData = NativeInterface.getSenderEncryptedMessage(senderHandle, messageCount);
        return otData;
    }

    // return otDataInput
    public OtData step2ExtendSeedPack(OtData otDataInput) {
        OtData otData = NativeInterface.step2SenderExtendSeedPack(senderHandle, otDataInput);
        return otData;
    }

    public OtData step4SetMatrix(OtData otDataInput) {
        OtData otData = NativeInterface.step4SenderSetMatrix(senderHandle, otDataInput, choiceCount, messageCount);
        // set EncMessage and send it to Receiver
        OtData newData = getSenderEncryptedMessage();
        otData.enMessage = newData.enMessage;
        otData.hash = newData.hash;
        return otData;
    }

    public void free() {
        NativeInterface.freeSender(senderHandle);
    }

}
