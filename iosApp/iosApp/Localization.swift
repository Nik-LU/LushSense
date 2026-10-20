import Foundation

enum AppText {
    static let appName = String(localized: "app_name")
    static let chatSubtitle = String(localized: "chat_subtitle")
    static let welcomeTagline = String(localized: "welcome_tagline")
    static let welcomeAction = String(localized: "welcome_action")
    static let welcomeDescription = String(localized: "welcome_description")
    static let chatEmptyState = String(localized: "chat_empty_state")
    static let chatInputPlaceholder = String(localized: "chat_input_placeholder")
    static let chatLoading = String(localized: "chat_loading")

    static func chatConfidence(_ value: Int) -> String {
        String(format: String(localized: "chat_confidence"), value)
    }
}
