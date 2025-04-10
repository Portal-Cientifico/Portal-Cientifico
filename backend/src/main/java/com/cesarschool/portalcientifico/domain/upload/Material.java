package com.cesarschool.portalcientifico.domain.upload;

import com.cesarschool.portalcientifico.domain.user.User;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeMaterial type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Area area;

    @ElementCollection
    private List<String> keywords;

    @Column(nullable = false)
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_download", columnDefinition = "integer default 0")
    private int totalDownload;

    @Column(name = "total_view", columnDefinition = "integer default 0")
    private int totalView;

    @Column(name = "upload_date", columnDefinition = "timestamp default now()")
    private LocalDateTime uploadDate;

    @Column(name = "creation_date", columnDefinition = "timestamp default now()")
    private LocalDateTime creationDate;
}
