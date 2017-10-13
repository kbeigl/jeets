package org.jeets.player;

public class PlaybackThread implements Runnable {
    
    public void run() {
        
    }
    
/*
    public void run() {
        System.out.println("Starting MessageLoop thread");
        messageLoopRunning = true; // TODO: do this right!
        try {
            // only transmit if queue has more msgs
            while (!messageQueue.isEmpty()) {
                boolean transmitted = false;
                // try transmitting until msgs are acknowledged
                while (!transmitted) {
                    // keep trying and add newly queued msgs until
                    // maxNrOfPositions
                    fillDeviceBuilder();
                    System.out.println("'" + uniqueId + "' sending " + devBuilder.getPositionCount() + " Positions to '"
                            + host + ":" + port + "' at " + new Date().getTime() + " (" + messageQueue.size()
                            + " msgs queued)");
                    if (transmitTraccarDevice(devBuilder)) {
                        transmitted = true;
                        devBuilder = null;
                        // reset and continue with new msgs from queue, fillDev,
                        // transmit
                    } else {
                        System.err.println("Transmission failed, trying again in 10 seconds");
                        Thread.sleep(10000); // int tryAgainInMillis = 10000;
                    }
                }
            }
        } catch (InterruptedException e) {
            System.err.println("MessageLoop was interrupted!");
            e.printStackTrace();
        }
        messageLoopRunning = false;
        System.out.println("MessageLoop done.");
    }
 */
}