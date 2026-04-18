using System.Net.Http.Json;
using ExpenseTrackerUser.Models;

namespace ExpenseTrackerUser.Services
{
    public class ApiService
    {
        private readonly HttpClient _httpClient;
        private const string BaseUrl = "http://10.0.2.2:3000/"; // Emulator IP

        public ApiService()
        {
            _httpClient = new HttpClient { BaseAddress = new Uri(BaseUrl) };
        }

        public async Task<List<Project>> GetProjectsAsync() =>
            await _httpClient.GetFromJsonAsync<List<Project>>("projects") ?? new();

        public async Task<List<Expense>> GetExpensesAsync(int projectId) =>
            await _httpClient.GetFromJsonAsync<List<Expense>>($"projects/{projectId}/expenses") ?? new();
    }
}