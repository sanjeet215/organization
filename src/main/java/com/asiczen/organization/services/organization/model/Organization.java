package com.asiczen.organization.services.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "organization")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Organization extends AuditModel implements Serializable {

	private static final long serialVersionUID = 2782735998562964416L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "org_id")
	private Long orgId;

	private String orgRefName;

	private String orgName;

	private String description;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "org_address_id")
	private Address address;

	private boolean status;

	private String imageUrl;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "organization" ,optional = true)
	private OrgParameters params;

}
