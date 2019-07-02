package com.rome.common.rpc.message;

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
    comments = "Source: MessageRPC.proto")
public final class VerificationCodeServiceGrpc {

  private VerificationCodeServiceGrpc() {}

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

  public static final String SERVICE_NAME = "com.rome.common.rpc.message.VerificationCodeService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.rome.common.rpc.message.VerificationCodeReq,
      com.rome.common.rpc.message.VerificationCodeRes> getGetVerificationCodeMethod;

  public static io.grpc.MethodDescriptor<com.rome.common.rpc.message.VerificationCodeReq,
      com.rome.common.rpc.message.VerificationCodeRes> getGetVerificationCodeMethod() {
    io.grpc.MethodDescriptor<com.rome.common.rpc.message.VerificationCodeReq, com.rome.common.rpc.message.VerificationCodeRes> getGetVerificationCodeMethod;
    if ((getGetVerificationCodeMethod = VerificationCodeServiceGrpc.getGetVerificationCodeMethod) == null) {
      synchronized (VerificationCodeServiceGrpc.class) {
        if ((getGetVerificationCodeMethod = VerificationCodeServiceGrpc.getGetVerificationCodeMethod) == null) {
          VerificationCodeServiceGrpc.getGetVerificationCodeMethod = getGetVerificationCodeMethod = 
              io.grpc.MethodDescriptor.<com.rome.common.rpc.message.VerificationCodeReq, com.rome.common.rpc.message.VerificationCodeRes>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.rome.common.rpc.message.VerificationCodeService", "getVerificationCode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.rome.common.rpc.message.VerificationCodeReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.rome.common.rpc.message.VerificationCodeRes.getDefaultInstance()))
                  .setSchemaDescriptor(new VerificationCodeServiceMethodDescriptorSupplier("getVerificationCode"))
                  .build();
          }
        }
     }
     return getGetVerificationCodeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static VerificationCodeServiceStub newStub(io.grpc.Channel channel) {
    return new VerificationCodeServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static VerificationCodeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new VerificationCodeServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static VerificationCodeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new VerificationCodeServiceFutureStub(channel);
  }

  /**
   * Creates a new vertx stub that supports all call types for the service
   */
  public static VerificationCodeServiceVertxStub newVertxStub(io.grpc.Channel channel) {
    return new VerificationCodeServiceVertxStub(channel);
  }

  /**
   */
  public static abstract class VerificationCodeServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getVerificationCode(com.rome.common.rpc.message.VerificationCodeReq request,
        io.grpc.stub.StreamObserver<com.rome.common.rpc.message.VerificationCodeRes> responseObserver) {
      asyncUnimplementedUnaryCall(getGetVerificationCodeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetVerificationCodeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.rome.common.rpc.message.VerificationCodeReq,
                com.rome.common.rpc.message.VerificationCodeRes>(
                  this, METHODID_GET_VERIFICATION_CODE)))
          .build();
    }
  }

  /**
   */
  public static final class VerificationCodeServiceStub extends io.grpc.stub.AbstractStub<VerificationCodeServiceStub> {
    public VerificationCodeServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    public VerificationCodeServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VerificationCodeServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VerificationCodeServiceStub(channel, callOptions);
    }

    /**
     */
    public void getVerificationCode(com.rome.common.rpc.message.VerificationCodeReq request,
        io.grpc.stub.StreamObserver<com.rome.common.rpc.message.VerificationCodeRes> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetVerificationCodeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class VerificationCodeServiceBlockingStub extends io.grpc.stub.AbstractStub<VerificationCodeServiceBlockingStub> {
    public VerificationCodeServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    public VerificationCodeServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VerificationCodeServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VerificationCodeServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.rome.common.rpc.message.VerificationCodeRes getVerificationCode(com.rome.common.rpc.message.VerificationCodeReq request) {
      return blockingUnaryCall(
          getChannel(), getGetVerificationCodeMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class VerificationCodeServiceFutureStub extends io.grpc.stub.AbstractStub<VerificationCodeServiceFutureStub> {
    public VerificationCodeServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    public VerificationCodeServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VerificationCodeServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VerificationCodeServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.rome.common.rpc.message.VerificationCodeRes> getVerificationCode(
        com.rome.common.rpc.message.VerificationCodeReq request) {
      return futureUnaryCall(
          getChannel().newCall(getGetVerificationCodeMethod(), getCallOptions()), request);
    }
  }

  /**
   */
  public static abstract class VerificationCodeServiceVertxImplBase implements io.grpc.BindableService {

    /**
     */
    public void getVerificationCode(com.rome.common.rpc.message.VerificationCodeReq request,
        io.vertx.core.Future<com.rome.common.rpc.message.VerificationCodeRes> response) {
      asyncUnimplementedUnaryCall(getGetVerificationCodeMethod(), VerificationCodeServiceGrpc.toObserver(response.completer()));
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetVerificationCodeMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                com.rome.common.rpc.message.VerificationCodeReq,
                com.rome.common.rpc.message.VerificationCodeRes>(
                  this, METHODID_GET_VERIFICATION_CODE)))
          .build();
    }
  }

  /**
   */
  public static final class VerificationCodeServiceVertxStub extends io.grpc.stub.AbstractStub<VerificationCodeServiceVertxStub> {
    public VerificationCodeServiceVertxStub(io.grpc.Channel channel) {
      super(channel);
    }

    public VerificationCodeServiceVertxStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VerificationCodeServiceVertxStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VerificationCodeServiceVertxStub(channel, callOptions);
    }

    /**
     */
    public void getVerificationCode(com.rome.common.rpc.message.VerificationCodeReq request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<com.rome.common.rpc.message.VerificationCodeRes>> response) {
      asyncUnaryCall(
          getChannel().newCall(getGetVerificationCodeMethod(), getCallOptions()), request, VerificationCodeServiceGrpc.toObserver(response));
    }
  }

  private static final int METHODID_GET_VERIFICATION_CODE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final VerificationCodeServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(VerificationCodeServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_VERIFICATION_CODE:
          serviceImpl.getVerificationCode((com.rome.common.rpc.message.VerificationCodeReq) request,
              (io.grpc.stub.StreamObserver<com.rome.common.rpc.message.VerificationCodeRes>) responseObserver);
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
    private final VerificationCodeServiceVertxImplBase serviceImpl;
    private final int methodId;

    VertxMethodHandlers(VerificationCodeServiceVertxImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_VERIFICATION_CODE:
          serviceImpl.getVerificationCode((com.rome.common.rpc.message.VerificationCodeReq) request,
              (io.vertx.core.Future<com.rome.common.rpc.message.VerificationCodeRes>) io.vertx.core.Future.<com.rome.common.rpc.message.VerificationCodeRes>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<com.rome.common.rpc.message.VerificationCodeRes>) responseObserver).onNext(ar.result());
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

  private static abstract class VerificationCodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    VerificationCodeServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.rome.common.rpc.message.MessageRPC.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("VerificationCodeService");
    }
  }

  private static final class VerificationCodeServiceFileDescriptorSupplier
      extends VerificationCodeServiceBaseDescriptorSupplier {
    VerificationCodeServiceFileDescriptorSupplier() {}
  }

  private static final class VerificationCodeServiceMethodDescriptorSupplier
      extends VerificationCodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    VerificationCodeServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (VerificationCodeServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new VerificationCodeServiceFileDescriptorSupplier())
              .addMethod(getGetVerificationCodeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
