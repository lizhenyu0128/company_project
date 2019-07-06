package com.rome.common.rpc.basic;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.20.0)",
    comments = "Source: Basic.proto")
public final class FileUploadServiceGrpc {

  private FileUploadServiceGrpc() {}

  private static <T> io.grpc.stub.StreamObserver<T> toObserver(final io.vertx.core.Handler<io.vertx.core.AsyncResult<T>> handler) {
    return new io.grpc.stub.StreamObserver<T>() {
      private volatile boolean resolved = false;
      @Override
      public void onNext(T value) {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.succeededFuture(value));
        }
      }

      @Override
      public void onError(Throwable t) {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.failedFuture(t));
        }
      }

      @Override
      public void onCompleted() {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.succeededFuture());
        }
      }
    };
  }

  public static final String SERVICE_NAME = "com.rome.common.rpc.basic.FileUploadService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.rome.common.rpc.basic.FileUploadReq,
      com.rome.common.rpc.basic.FileUploadRes> getFileUploadMethod;

  public static io.grpc.MethodDescriptor<com.rome.common.rpc.basic.FileUploadReq,
      com.rome.common.rpc.basic.FileUploadRes> getFileUploadMethod() {
    io.grpc.MethodDescriptor<com.rome.common.rpc.basic.FileUploadReq, com.rome.common.rpc.basic.FileUploadRes> getFileUploadMethod;
    if ((getFileUploadMethod = FileUploadServiceGrpc.getFileUploadMethod) == null) {
      synchronized (FileUploadServiceGrpc.class) {
        if ((getFileUploadMethod = FileUploadServiceGrpc.getFileUploadMethod) == null) {
          FileUploadServiceGrpc.getFileUploadMethod = getFileUploadMethod = 
              io.grpc.MethodDescriptor.<com.rome.common.rpc.basic.FileUploadReq, com.rome.common.rpc.basic.FileUploadRes>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.rome.common.rpc.basic.FileUploadService", "fileUpload"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.rome.common.rpc.basic.FileUploadReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.rome.common.rpc.basic.FileUploadRes.getDefaultInstance()))
                  .setSchemaDescriptor(new FileUploadServiceMethodDescriptorSupplier("fileUpload"))
                  .build();
          }
        }
     }
     return getFileUploadMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FileUploadServiceStub newStub(io.grpc.Channel channel) {
    return new FileUploadServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FileUploadServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new FileUploadServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FileUploadServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new FileUploadServiceFutureStub(channel);
  }

  /**
   * Creates a new vertx stub that supports all call types for the service
   */
  public static FileUploadServiceVertxStub newVertxStub(io.grpc.Channel channel) {
    return new FileUploadServiceVertxStub(channel);
  }

  /**
   */
  public static abstract class FileUploadServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void fileUpload(com.rome.common.rpc.basic.FileUploadReq request,
        io.grpc.stub.StreamObserver<com.rome.common.rpc.basic.FileUploadRes> responseObserver) {
      asyncUnimplementedUnaryCall(getFileUploadMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getFileUploadMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.rome.common.rpc.basic.FileUploadReq,
                com.rome.common.rpc.basic.FileUploadRes>(
                  this, METHODID_FILE_UPLOAD)))
          .build();
    }
  }

  /**
   */
  public static final class FileUploadServiceStub extends io.grpc.stub.AbstractStub<FileUploadServiceStub> {
    public FileUploadServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    public FileUploadServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileUploadServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FileUploadServiceStub(channel, callOptions);
    }

    /**
     */
    public void fileUpload(com.rome.common.rpc.basic.FileUploadReq request,
        io.grpc.stub.StreamObserver<com.rome.common.rpc.basic.FileUploadRes> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFileUploadMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class FileUploadServiceBlockingStub extends io.grpc.stub.AbstractStub<FileUploadServiceBlockingStub> {
    public FileUploadServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    public FileUploadServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileUploadServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FileUploadServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.rome.common.rpc.basic.FileUploadRes fileUpload(com.rome.common.rpc.basic.FileUploadReq request) {
      return blockingUnaryCall(
          getChannel(), getFileUploadMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class FileUploadServiceFutureStub extends io.grpc.stub.AbstractStub<FileUploadServiceFutureStub> {
    public FileUploadServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    public FileUploadServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileUploadServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FileUploadServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.rome.common.rpc.basic.FileUploadRes> fileUpload(
        com.rome.common.rpc.basic.FileUploadReq request) {
      return futureUnaryCall(
          getChannel().newCall(getFileUploadMethod(), getCallOptions()), request);
    }
  }

  /**
   */
  public static abstract class FileUploadServiceVertxImplBase implements io.grpc.BindableService {

    /**
     */
    public void fileUpload(com.rome.common.rpc.basic.FileUploadReq request,
        io.vertx.core.Future<com.rome.common.rpc.basic.FileUploadRes> response) {
      asyncUnimplementedUnaryCall(getFileUploadMethod(), FileUploadServiceGrpc.toObserver(response.completer()));
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getFileUploadMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                com.rome.common.rpc.basic.FileUploadReq,
                com.rome.common.rpc.basic.FileUploadRes>(
                  this, METHODID_FILE_UPLOAD)))
          .build();
    }
  }

  /**
   */
  public static final class FileUploadServiceVertxStub extends io.grpc.stub.AbstractStub<FileUploadServiceVertxStub> {
    public FileUploadServiceVertxStub(io.grpc.Channel channel) {
      super(channel);
    }

    public FileUploadServiceVertxStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileUploadServiceVertxStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FileUploadServiceVertxStub(channel, callOptions);
    }

    /**
     */
    public void fileUpload(com.rome.common.rpc.basic.FileUploadReq request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<com.rome.common.rpc.basic.FileUploadRes>> response) {
      asyncUnaryCall(
          getChannel().newCall(getFileUploadMethod(), getCallOptions()), request, FileUploadServiceGrpc.toObserver(response));
    }
  }

  private static final int METHODID_FILE_UPLOAD = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final FileUploadServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(FileUploadServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_FILE_UPLOAD:
          serviceImpl.fileUpload((com.rome.common.rpc.basic.FileUploadReq) request,
              (io.grpc.stub.StreamObserver<com.rome.common.rpc.basic.FileUploadRes>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class VertxMethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final FileUploadServiceVertxImplBase serviceImpl;
    private final int methodId;

    VertxMethodHandlers(FileUploadServiceVertxImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_FILE_UPLOAD:
          serviceImpl.fileUpload((com.rome.common.rpc.basic.FileUploadReq) request,
              (io.vertx.core.Future<com.rome.common.rpc.basic.FileUploadRes>) io.vertx.core.Future.<com.rome.common.rpc.basic.FileUploadRes>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<com.rome.common.rpc.basic.FileUploadRes>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class FileUploadServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FileUploadServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.rome.common.rpc.basic.Basic.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FileUploadService");
    }
  }

  private static final class FileUploadServiceFileDescriptorSupplier
      extends FileUploadServiceBaseDescriptorSupplier {
    FileUploadServiceFileDescriptorSupplier() {}
  }

  private static final class FileUploadServiceMethodDescriptorSupplier
      extends FileUploadServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    FileUploadServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (FileUploadServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FileUploadServiceFileDescriptorSupplier())
              .addMethod(getFileUploadMethod())
              .build();
        }
      }
    }
    return result;
  }
}
