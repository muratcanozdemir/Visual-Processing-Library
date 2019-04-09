/*   1:    */ package vpt.algorithms.io;
/*   2:    */ 
/*   3:    */ import com.xuggle.xuggler.ICodec.Type;
/*   4:    */ import com.xuggle.xuggler.IContainer;
/*   5:    */ import com.xuggle.xuggler.IContainer.Type;
/*   6:    */ import com.xuggle.xuggler.IPacket;
/*   7:    */ import com.xuggle.xuggler.IPixelFormat.Type;
/*   8:    */ import com.xuggle.xuggler.IStream;
/*   9:    */ import com.xuggle.xuggler.IStreamCoder;
/*  10:    */ import com.xuggle.xuggler.IVideoPicture;
/*  11:    */ import com.xuggle.xuggler.IVideoResampler;
/*  12:    */ import com.xuggle.xuggler.IVideoResampler.Feature;
/*  13:    */ import com.xuggle.xuggler.Utils;
/*  14:    */ import java.awt.image.BufferedImage;
/*  15:    */ import java.io.PrintStream;
/*  16:    */ import java.util.ArrayList;
/*  17:    */ import vpt.Algorithm;
/*  18:    */ import vpt.GlobalException;
/*  19:    */ import vpt.Image;
/*  20:    */ import vpt.util.Tools;
/*  21:    */ 
/*  22:    */ public class LoadAVI
/*  23:    */   extends Algorithm
/*  24:    */ {
/*  25: 30 */   public Image[] output = null;
/*  26: 31 */   public String path = null;
/*  27: 33 */   private boolean debug = false;
/*  28:    */   
/*  29:    */   public LoadAVI()
/*  30:    */   {
/*  31: 36 */     this.inputFields = "path";
/*  32: 37 */     this.outputFields = "output";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void execute()
/*  36:    */     throws GlobalException
/*  37:    */   {
/*  38: 41 */     ArrayList<Image> data = new ArrayList();
/*  39: 44 */     if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
/*  40: 45 */       throw new RuntimeException("you must install the GPL version of Xuggler (with IVideoResampler support) for this to work");
/*  41:    */     }
/*  42: 47 */     IContainer container = IContainer.make();
/*  43: 49 */     if (container.open(this.path, IContainer.Type.READ, null) < 0) {
/*  44: 50 */       throw new IllegalArgumentException("could not open file: " + this.path);
/*  45:    */     }
/*  46: 53 */     int numStreams = container.getNumStreams();
/*  47: 54 */     if (this.debug) {
/*  48: 54 */       System.err.println(numStreams);
/*  49:    */     }
/*  50: 57 */     int videoStreamId = -1;
/*  51: 58 */     IStreamCoder videoCoder = null;
/*  52: 60 */     for (int i = 0; i < numStreams; i++)
/*  53:    */     {
/*  54: 61 */       IStream stream = container.getStream(i);
/*  55: 62 */       IStreamCoder coder = stream.getStreamCoder();
/*  56: 64 */       if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
/*  57:    */       {
/*  58: 65 */         videoStreamId = i;
/*  59: 66 */         videoCoder = coder;
/*  60: 67 */         break;
/*  61:    */       }
/*  62:    */     }
/*  63: 71 */     if (videoStreamId == -1) {
/*  64: 72 */       throw new RuntimeException("could not find video stream in container: " + this.path);
/*  65:    */     }
/*  66: 75 */     if (videoCoder.open() < 0) {
/*  67: 76 */       throw new RuntimeException("could not open video decoder for container: " + this.path);
/*  68:    */     }
/*  69: 78 */     IVideoResampler resampler = null;
/*  70: 79 */     if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24)
/*  71:    */     {
/*  72: 81 */       resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
/*  73: 82 */       if (resampler == null) {
/*  74: 83 */         throw new RuntimeException("could not create color space resampler for: " + this.path);
/*  75:    */       }
/*  76:    */     }
/*  77: 86 */     IPacket packet = IPacket.make();
/*  78: 88 */     while ((container.readNextPacket(packet) >= 0) && (data.size() <= 10)) {
/*  79: 90 */       if (packet.getStreamIndex() == videoStreamId)
/*  80:    */       {
/*  81: 92 */         IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
/*  82:    */         
/*  83: 94 */         int offset = 0;
/*  84: 95 */         while (offset < packet.getSize())
/*  85:    */         {
/*  86: 97 */           int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
/*  87: 99 */           if (bytesDecoded < 0) {
/*  88: 99 */             throw new RuntimeException("got error decoding video in: " + this.path);
/*  89:    */           }
/*  90:101 */           offset += bytesDecoded;
/*  91:105 */           if (picture.isComplete())
/*  92:    */           {
/*  93:107 */             IVideoPicture newPic = picture;
/*  94:111 */             if (resampler != null)
/*  95:    */             {
/*  96:113 */               newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
/*  97:115 */               if (resampler.resample(newPic, picture) < 0) {
/*  98:116 */                 throw new RuntimeException("could not resample video from: " + this.path);
/*  99:    */               }
/* 100:    */             }
/* 101:118 */             if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
/* 102:119 */               throw new RuntimeException("could not decode video as BGR 24 bit data in: " + this.path);
/* 103:    */             }
/* 104:122 */             BufferedImage javaImage = Utils.videoPictureToImage(newPic);
/* 105:123 */             data.add(Tools.BufferedImage2Image(javaImage));
/* 106:    */           }
/* 107:    */         }
/* 108:    */       }
/* 109:    */     }
/* 110:133 */     this.output = new Image[data.size()];
/* 111:135 */     for (int i = 0; i < this.output.length; i++) {
/* 112:136 */       this.output[i] = ((Image)data.get(i));
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public static Image[] invoke(String path)
/* 117:    */   {
/* 118:    */     try
/* 119:    */     {
/* 120:150 */       return (Image[])new LoadAVI().preprocess(new Object[] { path });
/* 121:    */     }
/* 122:    */     catch (GlobalException e)
/* 123:    */     {
/* 124:153 */       e.printStackTrace();
/* 125:    */     }
/* 126:154 */     return null;
/* 127:    */   }
/* 128:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.io.LoadAVI
 * JD-Core Version:    0.7.0.1
 */