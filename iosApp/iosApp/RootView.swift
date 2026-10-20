import SwiftUI

struct RootView: View {
    @State private var path: [Route] = []

    var body: some View {
        NavigationStack(path: $path) {
            WelcomeView(onNavigateToChat: {
                guard path.last != .chat else { return }
                path.append(.chat)
            })
                .navigationDestination(for: Route.self) { route in
                    switch route {
                    case .chat:
                        ChatView()
                    }
                }
        }
    }
}
