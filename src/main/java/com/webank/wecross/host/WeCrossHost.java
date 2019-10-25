package com.webank.wecross.host;

import com.webank.wecross.network.NetworkManager;
import com.webank.wecross.p2p.P2PMessage;
import com.webank.wecross.peer.PeerManager;
import com.webank.wecross.resource.Path;
import com.webank.wecross.resource.Resource;
import com.webank.wecross.resource.TestResource;
import com.webank.wecross.stub.StateRequest;
import com.webank.wecross.stub.StateResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeCrossHost {

    private Logger logger = LoggerFactory.getLogger(WeCrossHost.class);

    private NetworkManager networkManager;
    private PeerManager peerManager;

    public void start() {
        addTestResources();
        peerManager.start();

        final long timeInterval = 5000;
        Runnable runnable =
                new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                // addSomeTestResources(200);
                                // workLoop();
                                // Thread.sleep(2);
                                // removeSomeTestResources(200);
                                workLoop();
                                Thread.sleep(timeInterval);
                            } catch (Exception e) {
                                logger.error("Startup error: " + e);
                                System.exit(-1);
                            }
                        }
                    }
                };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public Resource getResource(Path path) throws Exception {
        return networkManager.getResource(path);
    }

    public StateResponse getState(StateRequest request) {
        return networkManager.getState(request);
    }

    public Object onRestfulPeerMessage(String method, P2PMessage msg) {
        return peerManager.onRestfulPeerMessage(method, msg);
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public void setPeerManager(PeerManager peerManager) {
        this.peerManager = peerManager;
    }

    public void syncAllState() {}

    private void addTestResources() {
        try {
            logger.info("Add test resource");
            Path path = Path.decode("test-network.test-stub.test-resource");
            Resource resource = new TestResource();
            resource.setPath(path);
            networkManager.addResource(resource);

        } catch (Exception e) {
            logger.warn("Add test resource exception " + e);
        }
    }

    private void addManyTestResources(int num) {
        try {
            logger.info("Add resource");
            List<Integer> idList = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                idList.add(i);
            }
            Collections.shuffle(idList);
            for (int i : idList) {
                String name =
                        "test-network"
                                + (i / 100)
                                + ".test-stub"
                                + ((i / 10) % 10)
                                + ".test-resource"
                                + i % 10;
                Path path = Path.decode(name);
                Resource resource = new TestResource();
                resource.setPath(path);
                networkManager.addResource(resource);
            }
        } catch (Exception e) {
            logger.warn("Add resource exception " + e);
        }
    }

    private void removeManyTestResources(int num) {
        try {
            logger.info("Remove resource");
            List<Integer> idList = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                idList.add(i);
            }
            Collections.shuffle(idList);
            for (int i : idList) {
                String name =
                        "test-network"
                                + (i / 100)
                                + ".test-stub"
                                + ((i / 10) % 10)
                                + ".test-resource"
                                + i % 10;
                Path path = Path.decode(name);
                networkManager.removeResource(path);
            }
        } catch (Exception e) {
            logger.warn("Remove resource exception " + e);
        }
    }

    private void workLoop() {
        peerManager.broadcastSeqRequest();
        peerManager.syncWithPeerNetworks();
    }
}