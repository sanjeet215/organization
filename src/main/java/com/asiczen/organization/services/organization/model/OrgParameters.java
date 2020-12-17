package com.asiczen.organization.services.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "orgparams")
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgParameters extends AuditModel implements Serializable {

	private static final long serialVersionUID = 3435971401430430032L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "param_id")
	private Long paramId;

	private Integer overSpeedLimit = 80;
	private Integer underSpeedLimit = 20;
	private Integer fuelLimit = 10;
	private Integer underUtilizedHours = 2;
	private Integer overUtilizedHours = 12;
	private Integer underUtilizedKms = 50;
	private Integer overUtilizedKms = 500;

	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "org_id")
	@MapsId
	private Organization organization;

}
