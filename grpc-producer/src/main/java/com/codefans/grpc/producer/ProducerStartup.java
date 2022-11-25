package com.codefans.grpc.producer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: codefans
 * @Date: 2022-11-24 11:15
 */

public class ProducerStartup {

    @SneakyThrows
    public static void main(String[] args) {
        //启动gRPC Server
        Integer port = 50051;
        final ProducerStartup server = new ProducerStartup();
        server.start(port);
        server.blockUntilShutdown();

    }

    /**
     * gRPC server对象
     */
    private Server server;

    /**
     * 启动 gRPC服务端
     *
     * @param port 启动端口
     * @throws IOException
     */
    public void start(Integer port) throws IOException {
        //启动gRPC Server
        this.server = ServerBuilder.forPort(port)
                //注册服务端实现类
                .addService(new HelloGrpcImpl())
                .build()
                .start();
        System.out.println("gRPC Server started, listening on " + port);

        //注销、结束进程时关闭gRPC Server
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    ProducerStartup.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }


    /**
     * 关闭gRPC服务端
     *
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * 阻塞main线程，以使得gRPC一直运行提供服务
     * Await termination on the main thread since the grpc library uses daemon threads.
     *
     * @throws InterruptedException
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}
