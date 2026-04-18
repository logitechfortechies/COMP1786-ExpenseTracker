using ExpenseTrackerUser.Models;
using ExpenseTrackerUser.Services;

public partial class ProjectDetailPage : ContentPage
{
    private Project _project;
    private ApiService _api;

    public ProjectDetailPage(Project p, ApiService api)
    {
        InitializeComponent(); _project = p; _api = api;
        lblTitle.Text = p.ProjectName;
        lblDesc.Text = p.Description;
    }

    protected override async void OnAppearing()
    {
        cvExpenses.ItemsSource = await _api.GetExpensesAsync(_project.Id);
    }

    private async void OnAddExpenseClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new AddExpensePage(_project.Id, _api));
    }
}