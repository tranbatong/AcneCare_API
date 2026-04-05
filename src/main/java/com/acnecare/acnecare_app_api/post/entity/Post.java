package com.acnecare.acnecare_app_api.post.entity;


import java.time.LocalDateTime;
import java.util.List;

import com.acnecare.acnecare_app_api.identity.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Entity
@Table(name = "posts")
@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String postContent;
    String postTitle;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String status;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    User user;  

    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Likes> likes;

    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments;

}
