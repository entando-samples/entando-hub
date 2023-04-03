package com.entando.hub.catalog.persistence.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * This entity class is for BUNDLE table
 *
 */
@Entity
@Setter
@Getter
@Accessors(chain = true)
public class Bundle implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String gitRepoAddress;
    private String gitSrcRepoAddress;
    private String dependencies;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DescriptorVersion descriptorVersion = DescriptorVersion.V1;

	@ManyToMany(mappedBy = "bundles",fetch = FetchType.EAGER)
	private Set<BundleGroupVersion> bundleGroupVersions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bundle bundle = (Bundle) o;
        return Objects.equals(id, bundle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Bundle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", gitRepoAddress='" + gitRepoAddress + '\'' +
                ", dependencies='" + dependencies + '\'' +
                '}';
    }

}
