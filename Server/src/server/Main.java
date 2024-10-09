package server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.execute();
        server.awaitTermination(1000000);
        System.out.println("server has been started!");
    }
}