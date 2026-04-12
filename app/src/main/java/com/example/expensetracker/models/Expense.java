package com.example.expensetracker.models;
public class Expense {
    private int id;
    private int projectId;
    private String expenseCode;
    private String date;
    private double amount;
    private String currency;
    private String type;          // Travel, Equipment, Materials, etc.
    private String paymentMethod; // Cash, Credit Card, Bank Transfer, Cheque
    private String claimant;
    private String paymentStatus; // Paid, Pending, Reimbursed
    private String description;   // Optional
    private String location;      // Optional
    private int synced;

    public Expense() {}

    public Expense(int projectId, String expenseCode, String date, double amount,
                   String currency, String type, String paymentMethod,
                   String claimant, String paymentStatus,
                   String description, String location) {
        this.projectId = projectId;
        this.expenseCode = expenseCode;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.claimant = claimant;
        this.paymentStatus = paymentStatus;
        this.description = description;
        this.location = location;
        this.synced = 0;
    }

    public int getId()                { return id; }
    public int getProjectId()         { return projectId; }
    public String getExpenseCode()    { return expenseCode; }
    public String getDate()           { return date; }
    public double getAmount()         { return amount; }
    public String getCurrency()       { return currency; }
    public String getType()           { return type; }
    public String getPaymentMethod()  { return paymentMethod; }
    public String getClaimant()       { return claimant; }
    public String getPaymentStatus()  { return paymentStatus; }
    public String getDescription()    { return description; }
    public String getLocation()       { return location; }
    public int getSynced()            { return synced; }

    public void setId(int v)                  { this.id = v; }
    public void setProjectId(int v)           { this.projectId = v; }
    public void setExpenseCode(String v)      { this.expenseCode = v; }
    public void setDate(String v)             { this.date = v; }
    public void setAmount(double v)           { this.amount = v; }
    public void setCurrency(String v)         { this.currency = v; }
    public void setType(String v)             { this.type = v; }
    public void setPaymentMethod(String v)    { this.paymentMethod = v; }
    public void setClaimant(String v)         { this.claimant = v; }
    public void setPaymentStatus(String v)    { this.paymentStatus = v; }
    public void setDescription(String v)      { this.description = v; }
    public void setLocation(String v)         { this.location = v; }
    public void setSynced(int v)              { this.synced = v; }
}