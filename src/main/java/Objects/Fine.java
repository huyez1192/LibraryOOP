package Objects;
import java.util.Date;

public class Fine {
    private int fineId;
    private int userId;
    private int borrowId;
    private double fineAmount;
    private Date dueDate;

    // Constructors, getters and setters
    public Fine(int fineId, int userId, int borrowId, double fineAmount, Date dueDate) {
        this.fineId = fineId;
        this.userId = userId;
        this.borrowId = borrowId;
        this.fineAmount = fineAmount;
        this.dueDate = dueDate;
    }

    public int getFineId() {
        return fineId;
    }

    public void setFineId(int fineId) {
        this.fineId = fineId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}