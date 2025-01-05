package isaacwallace123.ecoverse.DataAccess;

import org.bson.Document;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id", unique = true, nullable = false)
    private String _id;

    @Column(name = "UUID", unique = true, nullable = false)
    private String UUID;

    @Column(name = "username")
    private String username;

    @Column(name = "balance")
    private double balance;

    public User() { }

    public User(String UUID) {
        this.UUID = UUID;
    }

    public User(String UUID, String username, double balance) {
        this.UUID = UUID;
        this.username = username;
        this.balance = balance;
    }

    public String getUUID() {
        return UUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Document toDocument() {
        return new Document("UUID", UUID)
                .append("username", username)
                .append("balance", balance);
    }

    public static User fromDocument(Document userDocument) {
        User user = new User(userDocument.getString("UUID"));

        user.setUsername(userDocument.getString("username"));
        user.setBalance(userDocument.getDouble("balance"));

        return user;
    }
}
