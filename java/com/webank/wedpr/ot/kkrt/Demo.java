package com.webank.wedpr.ot.kkrt;
import java.util.HashMap;

public class Demo {
    public static void main(String[] args) {

        long choiceCount = 2;
        long msgCount = 500;
        // init Sender
        Sender sender = new Sender();
        HashMap<Long, String> data = new HashMap<Long, String>();
        for(int i = 0; i < msgCount; i++) {
            data.put((long)123456+i, "WeDPR Test Message index:" + i);
        }
        sender.init(choiceCount, msgCount, data);

        // init Receiver
        Receiver receiver = new Receiver();
        long[] choice = new long[(int)choiceCount];
        for(int i = 0; i < choiceCount; i++) {
            choice[i] = (long)123476+i;
        }
        receiver.init(choiceCount, msgCount, choice);

        OtData otData = receiver.step1InitBaseOt();
        // Receiver otData => Sender
        otData = sender.step2ExtendSeedPack(otData);
        // Sender otData => Receiver
        otData = receiver.step3InitMatrix(otData);
        // Receiver otData => Sender
        otData = sender.step4SetMatrix(otData);
        // Sender otData => Receiver
        otData = receiver.step5GetResult(otData);
        for(int i=0; i < otData.data.length; i++) {
        System.out.println("result data = " + otData.data[i]);

        }

    }

}
