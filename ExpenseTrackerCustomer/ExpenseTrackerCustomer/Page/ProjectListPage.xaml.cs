using ExpenseTrackerUser.Models;
using ExpenseTrackerUser.Services;
using System.Collections.ObjectModel;

namespace ExpenseTrackerUser.Pages;

public partial class ProjectListPage : ContentPage
{
    private readonly ApiService _api;
    private readonly FavouritesService _favs;
    private List<Project> _allProjects = new();

    public Command<Project> ToggleFavCommand { get; }

    public ProjectListPage(ApiService api, FavouritesService favs)
    {
        InitializeComponent();
        _api = api;
        _favs = favs;
        ToggleFavCommand = new Command<Project>(p => {
            _favs.ToggleFavourite(p.Id);
            LoadProjects();
        });
        BindingContext = this;
    }

    protected override async void OnAppearing()
    {
        base.OnAppearing();
        await LoadProjects();
    }

    private async Task LoadProjects()
    {
        _allProjects = await _api.GetProjectsAsync();
        UpdateList(_allProjects);
    }

    private void UpdateList(List<Project> list)
    {
        var favIds = _favs.GetFavourites();
        // Add a helper property for the icon logic
        foreach (var p in list) p.FavIcon = favIds.Contains(p.Id) ? "star_filled.png" : "star_empty.png";
        cvProjects.ItemsSource = list;
    }

    private void OnSearchTextChanged(object sender, TextChangedEventArgs e)
    {
        UpdateList(_allProjects.Where(p => p.ProjectName.ToLower().Contains(e.NewTextValue.ToLower())).ToList());
    }

    private async void OnDetailsClicked(object sender, EventArgs e)
    {
        var p = (sender as Button).CommandParameter as Project;
        await Navigation.PushAsync(new ProjectDetailPage(p, _api));
    }
}
