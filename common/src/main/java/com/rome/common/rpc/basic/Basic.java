// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Basic.proto

package com.rome.common.rpc.basic;

public final class Basic {
  private Basic() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_rome_common_rpc_basic_FileUploadReq_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_rome_common_rpc_basic_FileUploadReq_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_rome_common_rpc_basic_FileUploadRes_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_rome_common_rpc_basic_FileUploadRes_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\013Basic.proto\022\031com.rome.common.rpc.basic" +
      "\"7\n\rFileUploadReq\022\021\n\timageByte\030\001 \001(\t\022\023\n\013" +
      "userAccount\030\002 \001(\t\"#\n\rFileUploadRes\022\022\n\nre" +
      "sultJson\030\001 \001(\t2u\n\021FileUploadService\022`\n\nf" +
      "ileUpload\022(.com.rome.common.rpc.basic.Fi" +
      "leUploadReq\032(.com.rome.common.rpc.basic." +
      "FileUploadResB\002P\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_rome_common_rpc_basic_FileUploadReq_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_rome_common_rpc_basic_FileUploadReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_rome_common_rpc_basic_FileUploadReq_descriptor,
        new java.lang.String[] { "ImageByte", "UserAccount", });
    internal_static_com_rome_common_rpc_basic_FileUploadRes_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_rome_common_rpc_basic_FileUploadRes_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_rome_common_rpc_basic_FileUploadRes_descriptor,
        new java.lang.String[] { "ResultJson", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
