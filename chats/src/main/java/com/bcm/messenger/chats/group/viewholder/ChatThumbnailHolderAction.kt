package com.bcm.messenger.chats.group.viewholder

import com.bcm.messenger.chats.R
import com.bcm.messenger.chats.components.ChatThumbnailView
import com.bcm.messenger.chats.group.logic.GroupMessageLogic
import com.bcm.messenger.chats.util.ChatPreviewClickListener
import com.bcm.messenger.common.AccountContext
import com.bcm.messenger.common.crypto.encrypt.BCMEncryptUtils
import com.bcm.messenger.common.grouprepository.model.AmeGroupMessageDetail
import com.bcm.messenger.common.mms.GlideRequests

/**
 *
 * Created by wjh on 2018/10/23
 */
class ChatThumbnailHolderAction() : BaseChatHolderAction<ChatThumbnailView>() {

    private var mPreviewClickListener = ChatPreviewClickListener()

    override fun bindData(accountContext: AccountContext, message: AmeGroupMessageDetail, body: ChatThumbnailView, glideRequests: GlideRequests, batchSelected: Set<AmeGroupMessageDetail>?) {

        mBaseView?.setThumbnailAppearance(R.drawable.common_image_place_img, R.drawable.common_image_broken_img, body.resources.getDimensionPixelSize(R.dimen.chats_conversation_item_radius))
        // MARK: Temporary set mastersecret
        mBaseView?.setImage(BCMEncryptUtils.getMasterSecret(accountContext) ?: return, glideRequests, message)
        mBaseView?.setThumbnailClickListener(mPreviewClickListener)
    }

    override fun unBind() {

    }

    override fun resend(accountContext: AccountContext, messageRecord: AmeGroupMessageDetail) {
        if(!messageRecord.attachmentUri.isNullOrEmpty()) {
            GroupMessageLogic.get(accountContext).messageSender.resendMediaMessage(messageRecord)
        }
    }

}