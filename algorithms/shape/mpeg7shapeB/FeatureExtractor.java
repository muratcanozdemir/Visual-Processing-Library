/*  1:   */ package vpt.algorithms.shape.mpeg7shapeB;
/*  2:   */ 
/*  3:   */ import java.io.BufferedWriter;
/*  4:   */ import java.io.FileWriter;
/*  5:   */ import java.io.PrintStream;
/*  6:   */ import java.io.PrintWriter;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.algorithms.io.Load;
/*  9:   */ import vpt.algorithms.statistical.InvariantMoment;
/* 10:   */ 
/* 11:   */ public class FeatureExtractor
/* 12:   */ {
/* 13:   */   public static void main(String[] args)
/* 14:   */   {
/* 15:38 */     String path = "/home/yoktish/workspace/mpeg7shapeB";
/* 16:39 */     int featureVectorLength = 1;
/* 17:   */     
/* 18:41 */     String[] classNames = { "apple", "bat", "beetle", "bell", "bird", "Bone", "bottle", "brick", "butterfly", "camel", "car", 
/* 19:42 */       "carriage", "cattle", "cellular_phone", "chicken", "children", "chopper", "classic", "Comma", "crown", "cup", "deer", "device0", "device1", "device2", 
/* 20:43 */       "device3", "device4", "device5", "device6", "device7", "device8", "device9", "dog", "elephant", "face", "fish", "flatfish", "fly", "fork", 
/* 21:44 */       "fountain", "frog", "Glas", "guitar", "hammer", "hat", "HCircle", "Heart", "horse", "horseshoe", "jar", "key", "lizzard", "lmfish", 
/* 22:45 */       "Misk", "octopus", "pencil", "personal_car", "pocket", "rat", "ray", "sea_snake", "shoe", "spoon", "spring", "stef", "teddy", "tree", "truck", 
/* 23:46 */       "turtle", "watch" };
/* 24:   */     try
/* 25:   */     {
/* 26:49 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("data.arff")));
/* 27:   */       
/* 28:51 */       pw.println("@RELATION mpeg7shape1-B");
/* 29:53 */       for (int i = 1; i <= featureVectorLength; i++) {
/* 30:54 */         pw.println("@ATTRIBUTE o" + i + "\tREAL");
/* 31:   */       }
/* 32:56 */       pw.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69}");
/* 33:57 */       pw.println("@DATA");
/* 34:59 */       for (int i = 0; i < classNames.length; i++) {
/* 35:61 */         for (int j = 1; j <= 20; j++)
/* 36:   */         {
/* 37:62 */           Image img = Load.invoke(path + "/" + classNames[i] + "-" + j + ".gif");
/* 38:   */           
/* 39:64 */           double[] feature = InvariantMoment.invoke(img, Integer.valueOf(1));
/* 40:66 */           for (int k = 0; k < feature.length; k++) {
/* 41:67 */             pw.print(feature[k] + ",");
/* 42:   */           }
/* 43:69 */           pw.println(i);
/* 44:   */           
/* 45:71 */           System.err.println(i + " " + j);
/* 46:   */         }
/* 47:   */       }
/* 48:74 */       pw.close();
/* 49:   */     }
/* 50:   */     catch (Exception e)
/* 51:   */     {
/* 52:76 */       e.printStackTrace();
/* 53:   */     }
/* 54:   */   }
/* 55:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.mpeg7shapeB.FeatureExtractor
 * JD-Core Version:    0.7.0.1
 */