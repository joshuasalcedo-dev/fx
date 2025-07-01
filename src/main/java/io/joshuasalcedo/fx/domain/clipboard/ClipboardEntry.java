package io.joshuasalcedo.fx.domain.clipboard;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "clipboard_entries",
    indexes = {@Index(name = "idx_timestamp", columnList = "timestamp")})
public class ClipboardEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT", nullable = false)
  @NotBlank(message = "Clipboard content cannot be empty")
  @Lob
  private String content;

  @Column(nullable = false)
  @NotNull(message = "Timestamp is required")
  private LocalDateTime timestamp = LocalDateTime.now();

  @Column(nullable = false)
  private boolean isPinned = false;

  @Column(name = "content_hash", length = 64)
  private String contentHash;

  @Column(name = "content_type")
  private String contentType = "text/plain";

  @Column(name = "content_length")
  private Integer contentLength;

  @PrePersist
  public void prePersist() {
    if (content != null) {
      this.contentLength = content.length();
      this.contentHash = generateContentHash();
    }
    if (timestamp == null) {
      this.timestamp = LocalDateTime.now();
    }
  }

  @PreUpdate
  public void preUpdate() {
    if (content != null) {
      this.contentLength = content.length();
      this.contentHash = generateContentHash();
    }
  }

  private String generateContentHash() {
    if (content == null) return null;
    // Simple hash using content only (timestamp makes entries unique even with same content)
    return String.valueOf(Math.abs(content.hashCode()));
  }

  public boolean isDuplicate(ClipboardEntry other) {
    return other != null && this.contentHash != null && this.contentHash.equals(other.contentHash);
  }

  // Constructors
  public ClipboardEntry() {}

  public ClipboardEntry(
      String content,
      LocalDateTime timestamp,
      boolean isPinned,
      String contentHash,
      String contentType,
      Integer contentLength) {
    this.content = content;
    this.timestamp = timestamp;
    this.isPinned = isPinned;
    this.contentHash = contentHash;
    this.contentType = contentType;
    this.contentLength = contentLength;
  }

  // Getters
  public Long getId() {
    return id;
  }

  public String getContent() {
    return content;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public boolean isPinned() {
    return isPinned;
  }

  public String getContentHash() {
    return contentHash;
  }

  public String getContentType() {
    return contentType;
  }

  public Integer getContentLength() {
    return contentLength;
  }

  // Setters
  public void setId(Long id) {
    this.id = id;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public void setPinned(boolean pinned) {
    this.isPinned = pinned;
  }

  public void setContentHash(String contentHash) {
    this.contentHash = contentHash;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void setContentLength(Integer contentLength) {
    this.contentLength = contentLength;
  }

  // Builder
  public static ClipboardEntryBuilder builder() {
    return new ClipboardEntryBuilder();
  }

  public static class ClipboardEntryBuilder {
    private Long id;
    private String content;
    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean isPinned = false;
    private String contentHash;
    private String contentType = "text/plain";
    private Integer contentLength;

    public ClipboardEntryBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public ClipboardEntryBuilder content(String content) {
      this.content = content;
      return this;
    }

    public ClipboardEntryBuilder timestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public ClipboardEntryBuilder isPinned(boolean isPinned) {
      this.isPinned = isPinned;
      return this;
    }

    public ClipboardEntryBuilder contentHash(String contentHash) {
      this.contentHash = contentHash;
      return this;
    }

    public ClipboardEntryBuilder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public ClipboardEntryBuilder contentLength(Integer contentLength) {
      this.contentLength = contentLength;
      return this;
    }

    public ClipboardEntry build() {
      ClipboardEntry entry =
          new ClipboardEntry(content, timestamp, isPinned, contentHash, contentType, contentLength);
      if (id != null) entry.setId(id);
      return entry;
    }
  }
}
