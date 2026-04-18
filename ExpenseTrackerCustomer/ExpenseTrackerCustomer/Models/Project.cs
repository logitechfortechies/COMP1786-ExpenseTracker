namespace ExpenseTrackerUser.Models
{
    public class Project
    {
        public int Id { get; set; }
        public string ProjectCode { get; set; }
        public string ProjectName { get; set; }
        public string Description { get; set; }
        public string StartDate { get; set; }
        public string EndDate { get; set; }
        public string Manager { get; set; }
        public string Status { get; set; }
        public double Budget { get; set; }
    }
}