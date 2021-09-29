/*
 * Copyright (c) 2021 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.home.room.detail.composer

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.features.home.room.detail.RoomDetailAction

sealed class TextComposerAction : VectorViewModelAction {
    data class SaveDraft(val draft: String) : TextComposerAction()
    data class SendMessage(val text: CharSequence, val autoMarkdown: Boolean) : TextComposerAction()
    data class EnterEditMode(val eventId: String, val text: String) : TextComposerAction()
    data class EnterQuoteMode(val eventId: String, val text: String) : TextComposerAction()
    data class EnterReplyMode(val eventId: String, val text: String) : TextComposerAction()
    data class EnterRegularMode(val text: String, val fromSharing: Boolean) : TextComposerAction()
    data class UserIsTyping(val isTyping: Boolean) : TextComposerAction()
    data class OnTextChanged(val text: CharSequence) : TextComposerAction()
    data class OnVoiceRecordingStateChanged(val isRecording: Boolean) : TextComposerAction()
}
