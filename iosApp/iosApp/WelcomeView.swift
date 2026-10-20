import SwiftUI
import SharedLogic

struct WelcomeView: View {
    let onNavigateToChat: () -> Void

    @State private var vm = WelcomeViewModelFactoryKt.createWelcomeViewModel()

    var body: some View {
        ZStack {
            Theme.background.ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer()

                Image("Logo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 96, height: 96)

                Text(AppText.appName)
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(Theme.textPrimary)
                    .padding(.top, 24)

                Text(AppText.welcomeTagline)
                    .font(.system(size: 15))
                    .foregroundColor(Theme.textSecondary)
                    .padding(.top, 8)

                Spacer().frame(height: 48)

                Button(action: { vm.onStartClicked() }) {
                    Text(AppText.welcomeAction)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(Theme.background)
                        .frame(maxWidth: .infinity, minHeight: 52)
                        .background(Theme.textPrimary)
                        .clipShape(RoundedRectangle(cornerRadius: 26))
                }

                Text(AppText.welcomeDescription)
                    .font(.system(size: 12))
                    .foregroundColor(Theme.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.top, 16)

                Spacer()
            }
            .padding(.horizontal, 32)
        }
        .preferredColorScheme(.dark)
        .task {
            for await event in vm.navigationEvents {
                if event is WelcomeNavigationEventNavigateToChat {
                    onNavigateToChat()
                }
            }
        }
    }
}
