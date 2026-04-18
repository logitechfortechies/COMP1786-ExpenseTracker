namespace ExpenseTrackerUser.Models
{
    public class Expense
    {
        public int Id { get; set; }
        public int ProjectId { get; set; }
        public string ExpenseCode { get; set; }
        public string Date { get; set; }
        public double Amount { get; set; }
        public string Currency { get; set; }
        public string Type { get; set; }
        public string PaymentMethod { get; set; }
        public string Claimant { get; set; }
        public string PaymentStatus { get; set; }
    }
}