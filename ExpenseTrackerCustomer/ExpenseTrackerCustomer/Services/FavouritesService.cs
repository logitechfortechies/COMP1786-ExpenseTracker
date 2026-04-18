using System.Text.Json;

namespace ExpenseTrackerUser.Services
{
    public class FavouritesService
    {
        private const string FavKey = "favourite_projects";

        public List<int> GetFavourites()
        {
            var json = Preferences.Default.Get(FavKey, "[]");
            return JsonSerializer.Deserialize<List<int>>(json) ?? new();
        }

        public void ToggleFavourite(int projectId)
        {
            var favs = GetFavourites();
            if (favs.Contains(projectId)) favs.Remove(projectId);
            else favs.Add(projectId);

            Preferences.Default.Set(FavKey, JsonSerializer.Serialize(favs));
        }
    }
}