using ExpenseTrackerUser.Models;
using ObjCBindings;
using System.Text;
using WebKit;

private async void OnSaveClicked(object sender, EventArgs e)
{
    var exp = new Expense
    {
        ProjectId = _projectId,
        ExpenseCode = entCode.Text,
        Amount = double.Parse(entAmount.Text),
        Date = dpDate.Date.ToString("yyyy-MM-dd"),
        Type = pkType.SelectedItem.ToString(),
        PaymentMethod = pkMethod.SelectedItem.ToString(),
        Claimant = entClaimant.Text,
        PaymentStatus = "Pending"
    };
    await _api.UploadExpenseAsync(exp);
    await Navigation.PopAsync();
}