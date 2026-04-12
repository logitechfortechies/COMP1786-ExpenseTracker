package com.example.expensetracker.models;
public class Project {
    private int id;
    private String projectCode;
    private String projectName;
    private String description;
    private String startDate;
    private String endDate;
    private String manager;
    private String status;           // "Active", "Completed", "On Hold"
    private double budget;
    private String specialRequirements; // Optional
    private String clientInfo;          // Optional
    private String additionalInfo;      // Optional — matches lecturer's "Additional Info"
    private int synced;                 // 0 = local, 1 = uploaded

    public Project() {}

    public Project(String projectCode, String projectName, String description,
                   String startDate, String endDate, String manager, String status,
                   double budget, String specialRequirements,
                   String clientInfo, String additionalInfo) {
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.manager = manager;
        this.status = status;
        this.budget = budget;
        this.specialRequirements = specialRequirements;
        this.clientInfo = clientInfo;
        this.additionalInfo = additionalInfo;
        this.synced = 0;
    }

    // ── Getters ──────────────────────────────────────────────
    public int getId()                   { return id; }
    public String getProjectCode()       { return projectCode; }
    public String getProjectName()       { return projectName; }
    public String getDescription()       { return description; }
    public String getStartDate()         { return startDate; }
    public String getEndDate()           { return endDate; }
    public String getManager()           { return manager; }
    public String getStatus()            { return status; }
    public double getBudget()            { return budget; }
    public String getSpecialRequirements() { return specialRequirements; }
    public String getClientInfo()        { return clientInfo; }
    public String getAdditionalInfo()    { return additionalInfo; }
    public int getSynced()               { return synced; }

    // ── Setters ──────────────────────────────────────────────
    public void setId(int id)                           { this.id = id; }
    public void setProjectCode(String v)                { this.projectCode = v; }
    public void setProjectName(String v)                { this.projectName = v; }
    public void setDescription(String v)                { this.description = v; }
    public void setStartDate(String v)                  { this.startDate = v; }
    public void setEndDate(String v)                    { this.endDate = v; }
    public void setManager(String v)                    { this.manager = v; }
    public void setStatus(String v)                     { this.status = v; }
    public void setBudget(double v)                     { this.budget = v; }
    public void setSpecialRequirements(String v)        { this.specialRequirements = v; }
    public void setClientInfo(String v)                 { this.clientInfo = v; }
    public void setAdditionalInfo(String v)             { this.additionalInfo = v; }
    public void setSynced(int v)                        { this.synced = v; }
}