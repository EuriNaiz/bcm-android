/**
 * Copyright (C) 2014-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */

package org.whispersystems.signalservice.api.messages;

import android.text.TextUtils;

import org.whispersystems.libsignal.util.guava.Optional;

import java.io.InputStream;

public abstract class SignalServiceAttachment {

  private final String contentType;
  private final String index;//attachment 在App端全局唯一索引

  protected SignalServiceAttachment(String contentType, String index) {
    this.contentType = contentType;
    this.index = index;
  }

  public String getContentType() {
    return contentType;
  }
  public String getIndex() {return index;}

  public abstract boolean isStream();
  public abstract boolean isPointer();

  public SignalServiceAttachmentStream asStream() {
    return (SignalServiceAttachmentStream)this;
  }

  public SignalServiceAttachmentPointer asPointer() {
    return (SignalServiceAttachmentPointer)this;
  }

  public static Builder newStreamBuilder() {
    return new Builder();
  }

  public static class Builder {

    private InputStream      inputStream;
    private String           contentType;
    private String           fileName;
    private long             length;
    private ProgressListener listener;
    private boolean          voiceNote;
    private String           index;

    private Builder() {}

    public Builder withStream(InputStream inputStream) {
      this.inputStream = inputStream;
      return this;
    }

    public Builder withContentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder withLength(long length) {
      this.length = length;
      return this;
    }

    public Builder withFileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public Builder withListener(ProgressListener listener) {
      this.listener = listener;
      return this;
    }

    public Builder withVoiceNote(boolean voiceNote) {
      this.voiceNote = voiceNote;
      return this;
    }

    public Builder withIndex(String index){
      this.index = index;
      return this;
    }

    public SignalServiceAttachmentStream build() {
      if (inputStream == null) throw new IllegalArgumentException("Must specify stream!");
      if (contentType == null) throw new IllegalArgumentException("No content type specified!");
      if (length == 0)         throw new IllegalArgumentException("No length specified!");
      if (TextUtils.isEmpty(index)) throw new IllegalArgumentException("index must specified!");

      return new SignalServiceAttachmentStream(inputStream, contentType, length, Optional.fromNullable(fileName), voiceNote, index, listener);
    }
  }

  /**
   * An interface to receive progress information on upload/download of
   * an attachment.
   */
  public interface ProgressListener {
    /**
     * Called on a progress change event.
     *
     * @param total The total amount to transmit/receive in bytes.
     * @param progress The amount that has been transmitted/received in bytes thus far
     */
    public void onAttachmentProgress(long total, long progress);
  }
}
