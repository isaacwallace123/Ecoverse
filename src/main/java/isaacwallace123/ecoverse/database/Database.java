package isaacwallace123.ecoverse.database;

public interface Database {
    void connect();
    void disconnect();
    boolean isConnected();
    double getBalance(String player);
    void setBalance(String player, double balance);
}
