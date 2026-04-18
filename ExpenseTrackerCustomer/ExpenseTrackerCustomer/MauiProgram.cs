using Microsoft.Extensions.Logging;

namespace ExpenseTrackerUser;

public static class MauiProgram
{
    public static MauiApp CreateMauiApp()
    {
        var builder = MauiApp.CreateBuilder();
        builder
            .UseMaui()
            .ConfigureFonts(fonts =>
            {
                fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
            });

        // Register HttpClient as singleton so all pages share one instance
        builder.Services.AddSingleton<HttpClient>();

#if DEBUG
        builder.Logging.AddDebug();
#endif

        return builder.Build();
    }
}