package com.webank.wedpr.ot.kkrt;
import java.util.HashMap;

public class Demo {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int choiceCount = 100;
        // message
        int messageCount = 10000;
        // init Sender
        Sender sender = new Sender();
        HashMap<Long, String> data = new HashMap<Long, String>();
        for(int i = 0; i < messageCount; i++) {
            data.put((long)123456+i, "WeDPR Test Message index:" + i);
        }
        sender.init(choiceCount, messageCount, data);

        // init Receiver
        Receiver receiver = new Receiver();
        long[] choice = new long[(int)choiceCount];
        for(int i = 0; i < choiceCount; i++) {
            choice[i] = (long)123456+20+i;
        }
        receiver.init(choiceCount, messageCount, choice);

        System.out.println("receiver step1InitBaseOt start");
        OtData otData = receiver.step1InitBaseOt();
        // Receiver otData => Sender
        System.out.println("receiver step2ExtendSeedPack start");
        otData = sender.step2ExtendSeedPack(otData);
        // Sender otData => Receiver
        System.out.println("receiver step3InitMatrix start");
        otData = receiver.step3InitMatrix(otData);
        // Receiver otData => Sender
        System.out.println("receiver step4SetMatrix start");
        otData = sender.step4SetMatrix(otData);
        // Sender otData => Receiver
        System.out.println("receiver step5GetResult start");
        // result data
        OtResult result = receiver.step5GetResult(otData);
        System.out.println("receiver step5GetResult end");
        for(int i=0; i < result.data.length; i++) {
            System.out.println("result data = " + result.data[i]);
        }
        long end = System.currentTimeMillis();
        System.out.println("choiceCount:"+ choiceCount + ", messageCount:" + messageCount + " run time = " + (end - start) + "ms");

        sender.free();
        receiver.free();
    }

}
