import Foundation
import SharedLogic

final class ChatViewModelHolder: ObservableObject {
    let vm: ChatViewModel = ChatViewModelFactoryKt.createChatViewModel()

    deinit {
        vm.cleanup()
    }
}
