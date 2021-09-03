/*
 * Azure Identity Labs API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * TempUser
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-09-02T18:14:28.452984-05:00[America/Chicago]")
public class TempUser {
  @SerializedName("title")
  private String title = null;

  @SerializedName("userType")
  private String userType = null;

  @SerializedName("upn")
  private String upn = null;

  @SerializedName("passwordUri")
  private String passwordUri = null;

  @SerializedName("credentialVaultKeyName")
  private String credentialVaultKeyName = null;

  @SerializedName("tenantId")
  private String tenantId = null;

  @SerializedName("tenantName")
  private String tenantName = null;

  @SerializedName("labName")
  private String labName = null;

  @SerializedName("tenantDomain")
  private String tenantDomain = null;

  @SerializedName("authority")
  private String authority = null;

  @SerializedName("objectId")
  private String objectId = null;

  public TempUser title(String title) {
    this.title = title;
    return this;
  }

   /**
   * Get title
   * @return title
  **/
  @Schema(description = "")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public TempUser userType(String userType) {
    this.userType = userType;
    return this;
  }

   /**
   * Get userType
   * @return userType
  **/
  @Schema(description = "")
  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public TempUser upn(String upn) {
    this.upn = upn;
    return this;
  }

   /**
   * Get upn
   * @return upn
  **/
  @Schema(description = "")
  public String getUpn() {
    return upn;
  }

  public void setUpn(String upn) {
    this.upn = upn;
  }

  public TempUser passwordUri(String passwordUri) {
    this.passwordUri = passwordUri;
    return this;
  }

   /**
   * Get passwordUri
   * @return passwordUri
  **/
  @Schema(description = "")
  public String getPasswordUri() {
    return passwordUri;
  }

  public void setPasswordUri(String passwordUri) {
    this.passwordUri = passwordUri;
  }

  public TempUser credentialVaultKeyName(String credentialVaultKeyName) {
    this.credentialVaultKeyName = credentialVaultKeyName;
    return this;
  }

   /**
   * Get credentialVaultKeyName
   * @return credentialVaultKeyName
  **/
  @Schema(description = "")
  public String getCredentialVaultKeyName() {
    return credentialVaultKeyName;
  }

  public void setCredentialVaultKeyName(String credentialVaultKeyName) {
    this.credentialVaultKeyName = credentialVaultKeyName;
  }

  public TempUser tenantId(String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

   /**
   * Get tenantId
   * @return tenantId
  **/
  @Schema(description = "")
  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public TempUser tenantName(String tenantName) {
    this.tenantName = tenantName;
    return this;
  }

   /**
   * Get tenantName
   * @return tenantName
  **/
  @Schema(description = "")
  public String getTenantName() {
    return tenantName;
  }

  public void setTenantName(String tenantName) {
    this.tenantName = tenantName;
  }

  public TempUser labName(String labName) {
    this.labName = labName;
    return this;
  }

   /**
   * Get labName
   * @return labName
  **/
  @Schema(description = "")
  public String getLabName() {
    return labName;
  }

  public void setLabName(String labName) {
    this.labName = labName;
  }

  public TempUser tenantDomain(String tenantDomain) {
    this.tenantDomain = tenantDomain;
    return this;
  }

   /**
   * Get tenantDomain
   * @return tenantDomain
  **/
  @Schema(description = "")
  public String getTenantDomain() {
    return tenantDomain;
  }

  public void setTenantDomain(String tenantDomain) {
    this.tenantDomain = tenantDomain;
  }

  public TempUser authority(String authority) {
    this.authority = authority;
    return this;
  }

   /**
   * Get authority
   * @return authority
  **/
  @Schema(description = "")
  public String getAuthority() {
    return authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
  }

  public TempUser objectId(String objectId) {
    this.objectId = objectId;
    return this;
  }

   /**
   * Get objectId
   * @return objectId
  **/
  @Schema(description = "")
  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TempUser tempUser = (TempUser) o;
    return Objects.equals(this.title, tempUser.title) &&
        Objects.equals(this.userType, tempUser.userType) &&
        Objects.equals(this.upn, tempUser.upn) &&
        Objects.equals(this.passwordUri, tempUser.passwordUri) &&
        Objects.equals(this.credentialVaultKeyName, tempUser.credentialVaultKeyName) &&
        Objects.equals(this.tenantId, tempUser.tenantId) &&
        Objects.equals(this.tenantName, tempUser.tenantName) &&
        Objects.equals(this.labName, tempUser.labName) &&
        Objects.equals(this.tenantDomain, tempUser.tenantDomain) &&
        Objects.equals(this.authority, tempUser.authority) &&
        Objects.equals(this.objectId, tempUser.objectId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, userType, upn, passwordUri, credentialVaultKeyName, tenantId, tenantName, labName, tenantDomain, authority, objectId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TempUser {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    userType: ").append(toIndentedString(userType)).append("\n");
    sb.append("    upn: ").append(toIndentedString(upn)).append("\n");
    sb.append("    passwordUri: ").append(toIndentedString(passwordUri)).append("\n");
    sb.append("    credentialVaultKeyName: ").append(toIndentedString(credentialVaultKeyName)).append("\n");
    sb.append("    tenantId: ").append(toIndentedString(tenantId)).append("\n");
    sb.append("    tenantName: ").append(toIndentedString(tenantName)).append("\n");
    sb.append("    labName: ").append(toIndentedString(labName)).append("\n");
    sb.append("    tenantDomain: ").append(toIndentedString(tenantDomain)).append("\n");
    sb.append("    authority: ").append(toIndentedString(authority)).append("\n");
    sb.append("    objectId: ").append(toIndentedString(objectId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
