import SwiftUI
import SharedLogic

struct ChatView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var holder = ChatViewModelHolder()
    @State private var uiState: ChatUiState?
    @State private var input: String = ""
    @State private var snackbar: String?

    var body: some View {
        VStack(spacing: 0) {
            topBar
            content
            inputBar
        }
        .background(Theme.background)
        .preferredColorScheme(.dark)
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
        .task {
            for await state in holder.vm.uiState {
                self.uiState = state
            }
        }
        .task {
            for await event in holder.vm.events {
                if let showError = event as? ChatEventShowError {
                    self.snackbar = showError.message
                }
            }
        }
        .overlay(alignment: .bottom) {
            if let text = snackbar {
                Text(text)
                    .font(.system(size: 13))
                    .foregroundColor(Theme.textPrimary)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 10)
                    .background(Theme.surface)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .padding(.bottom, 100)
                    .transition(.opacity)
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                            self.snackbar = nil
                        }
                    }
            }
        }
    }

    // ─── TopBar ────────────────────────────────────────
    private var topBar: some View {
        VStack(spacing: 0) {
            HStack(spacing: 10) {
                Button {
                    dismiss()
                } label: {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(Theme.textPrimary)
                        .frame(width: 32, height: 32)
                }

                Image("Logo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 30, height: 30)
                    .clipShape(Circle())

                VStack(alignment: .leading, spacing: 2) {
                    Text(AppText.appName)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(Theme.textPrimary)
                    Text(AppText.chatSubtitle)
                        .font(.system(size: 12))
                        .foregroundColor(Theme.textSecondary)
                }

                Spacer()

                Button {
                    holder.vm.clearChat()
                } label: {
                    Image(systemName: "trash")
                        .foregroundColor(Theme.textSecondary)
                }
                .disabled(!(uiState is ChatUiStateContent))
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(Theme.surface)

            Rectangle()
                .fill(Theme.divider)
                .frame(height: 1)
        }
    }

    // ─── Content ───────────────────────────────────────
    @ViewBuilder
    private var content: some View {
        if let state = uiState as? ChatUiStateContent {
            messagesList(state: state)
        } else if let state = uiState as? ChatUiStateError {
            errorView(message: state.message)
        } else {
            loadingView
        }
    }

    private func messagesList(state: ChatUiStateContent) -> some View {
        ScrollViewReader { proxy in
            ScrollView {
                LazyVStack(spacing: 10) {
                    if state.messages.isEmpty {
                        emptyState
                    } else {
                        ForEach(state.messages, id: \.id) { msg in
                            messageBubble(msg: msg).id(msg.id)
                        }
                    }
                    if state.isSending {
                        loadingBubble.id("loading")
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 16)
            }
            .onAppear {
                DispatchQueue.main.async {
                    withAnimation(.easeOut(duration: 0.25)) {
                        if let lastId = state.messages.last?.id {
                            proxy.scrollTo(lastId, anchor: .bottom)
                        }
                    }
                }
            }
            .onChange(of: state.messages.count) { oldValue, newValue in
                DispatchQueue.main.async {
                    withAnimation(.easeOut(duration: 0.25)) {
                        if let lastId = state.messages.last?.id {
                            proxy.scrollTo(lastId, anchor: .bottom)
                        }
                    }
                }
            }
            .onChange(of: state.isSending) { oldValue, newValue in
                if newValue {
                    DispatchQueue.main.async {
                        withAnimation(.easeOut(duration: 0.25)) {
                            proxy.scrollTo("loading", anchor: .bottom)
                        }
                    }
                }
            }
        }
    }

    private var emptyState: some View {
        VStack(spacing: 16) {
            ZStack {
                Circle()
                    .fill(Theme.surface)
                    .frame(width: 72, height: 72)
                Text("💬").font(.system(size: 32))
            }
            Text(AppText.chatEmptyState)
                .multilineTextAlignment(.center)
                .font(.system(size: 14))
                .foregroundColor(Theme.textSecondary)
                .padding(.horizontal, 40)
        }
        .frame(maxWidth: .infinity, minHeight: 400)
    }

    private var loadingView: some View {
        VStack {
            Spacer()
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle(tint: Theme.textSecondary))
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private func errorView(message: String) -> some View {
        VStack {
            Spacer()
            Text(message)
                .font(.system(size: 14))
                .foregroundColor(Theme.negative)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 32)
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    // ─── Bubble ────────────────────────────────────────
    private func messageBubble(msg: ChatMessage) -> some View {
        HStack {
            if msg.isFromUser { Spacer(minLength: 40) }

            VStack(alignment: msg.isFromUser ? .trailing : .leading, spacing: 4) {
                Text(displayText(msg))
                    .font(.system(size: 15, weight: (!msg.isFromUser && msg.positive != nil) ? .semibold : .regular))
                    .foregroundColor(textColor(msg))

                if let confidence = msg.confidence {
                    Text(AppText.chatConfidence(Int(confidence.doubleValue * 100)))
                        .font(.system(size: 12))
                        .foregroundColor(Theme.textSecondary)
                }
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 10)
            .background(msg.isFromUser ? Theme.userBubble : Theme.assistantBubble)
            .clipShape(
                UnevenRoundedRectangle(
                    topLeadingRadius: 18,
                    bottomLeadingRadius: msg.isFromUser ? 18 : 4,
                    bottomTrailingRadius: msg.isFromUser ? 4 : 18,
                    topTrailingRadius: 18
                )
            )

            if !msg.isFromUser { Spacer(minLength: 40) }
        }
    }

    private func displayText(_ msg: ChatMessage) -> String {
        if !msg.isFromUser, let positive = msg.positive {
            let emoji = positive.boolValue ? "😊" : "😔"
            return "\(emoji) \(msg.text)"
        }
        return msg.text
    }

    private func textColor(_ msg: ChatMessage) -> Color {
        if msg.isFromUser { return Theme.textPrimary }
        if let positive = msg.positive {
            return positive.boolValue ? Theme.positive : Theme.negative
        }
        return Theme.textPrimary
    }

    // ─── Loading bubble ────────────────────────────────
    private var loadingBubble: some View {
        HStack {
            HStack(spacing: 10) {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: Theme.textSecondary))
                    .scaleEffect(0.8)
                Text(AppText.chatLoading)
                    .font(.system(size: 14))
                    .foregroundColor(Theme.textSecondary)
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 10)
            .background(Theme.assistantBubble)
            .clipShape(RoundedRectangle(cornerRadius: 18))
            Spacer()
        }
    }

    // ─── Input ─────────────────────────────────────────
    private var inputBar: some View {
        let hasText = !input.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
        let isSending = (uiState as? ChatUiStateContent)?.isSending ?? false
        let canSend = hasText && !isSending

        return HStack(alignment: .bottom, spacing: 8) {
            TextField(AppText.chatInputPlaceholder, text: $input, axis: .vertical)
                .lineLimit(1...4)
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .background(Theme.inputBackground)
                .clipShape(RoundedRectangle(cornerRadius: 22))
                .foregroundColor(Theme.textPrimary)
                .tint(Theme.accent)

            Button {
                let text = input
                input = ""
                holder.vm.sendMessage(text: text)
            } label: {
                Image(systemName: "arrow.up")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(canSend ? Theme.background : Theme.textSecondary)
                    .frame(width: 44, height: 44)
                    .background(canSend ? Theme.accent : Theme.inputBackground)
                    .clipShape(Circle())
            }
            .disabled(!canSend)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 10)
        .background(Theme.background)
    }
}
