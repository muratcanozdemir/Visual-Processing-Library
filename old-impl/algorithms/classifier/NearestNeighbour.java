/*   1:    */ package vpt.algorithms.classifier;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.FileReader;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import vpt.util.Distance;
/*   7:    */ 
/*   8:    */ public class NearestNeighbour
/*   9:    */ {
/*  10:    */   public static void main(String[] args)
/*  11:    */   {
/*  12:    */     try
/*  13:    */     {
/*  14: 11 */       int featureSize = 116;
/*  15: 12 */       int trainSamples = 1098;
/*  16: 13 */       int testSamples = 1251;
/*  17: 14 */       int classCount = 61;
/*  18:    */       
/*  19: 16 */       BufferedReader trainFile = new BufferedReader(new FileReader("train.arff"));
/*  20: 17 */       BufferedReader testFile = new BufferedReader(new FileReader("test.arff"));
/*  21:    */       
/*  22: 19 */       double[][] trainFeatures = new double[trainSamples][featureSize];
/*  23: 20 */       String[] trainLabels = new String[trainSamples];
/*  24:    */       
/*  25: 22 */       double[][] testFeatures = new double[testSamples][featureSize];
/*  26: 23 */       String[] testLabels = new String[testSamples];
/*  27: 24 */       int[] numberOfSamplesPerClass = new int[classCount];
/*  28:    */       
/*  29: 26 */       String[] results = new String[testSamples];
/*  30:    */       
/*  31:    */ 
/*  32: 29 */       String s = null;
/*  33: 30 */       int sample = 0;
/*  34: 31 */       while ((s = trainFile.readLine()) != null)
/*  35:    */       {
/*  36: 32 */         String[] tokens = s.split(",");
/*  37: 34 */         for (int i = 0; i < featureSize; i++) {
/*  38: 35 */           trainFeatures[sample][i] = Double.parseDouble(tokens[i]);
/*  39:    */         }
/*  40: 37 */         trainLabels[sample] = tokens[featureSize];
/*  41: 38 */         sample++;
/*  42:    */       }
/*  43: 42 */       s = null;
/*  44: 43 */       sample = 0;
/*  45: 44 */       while ((s = testFile.readLine()) != null)
/*  46:    */       {
/*  47: 45 */         String[] tokens = s.split(",");
/*  48: 47 */         for (int i = 0; i < featureSize; i++) {
/*  49: 48 */           testFeatures[sample][i] = Double.parseDouble(tokens[i]);
/*  50:    */         }
/*  51: 50 */         testLabels[sample] = tokens[featureSize];
/*  52: 51 */         numberOfSamplesPerClass[java.lang.Integer.parseInt(testLabels[sample])] += 1;
/*  53: 52 */         sample++;
/*  54:    */       }
/*  55: 57 */       double[] max = new double[featureSize];
/*  56: 58 */       for (int i = 0; i < max.length; i++) {
/*  57: 59 */         max[i] = 0.0D;
/*  58:    */       }
/*  59: 62 */       double[] min = new double[featureSize];
/*  60: 63 */       for (int i = 0; i < max.length; i++) {
/*  61: 64 */         min[i] = 1.7976931348623157E+308D;
/*  62:    */       }
/*  63: 66 */       for (int i = 0; i < featureSize; i++) {
/*  64: 68 */         for (int j = 0; j < trainSamples; j++)
/*  65:    */         {
/*  66: 69 */           if (trainFeatures[j][i] > max[i]) {
/*  67: 69 */             max[i] = trainFeatures[j][i];
/*  68:    */           }
/*  69: 70 */           if (trainFeatures[j][i] < min[i]) {
/*  70: 70 */             min[i] = trainFeatures[j][i];
/*  71:    */           }
/*  72:    */         }
/*  73:    */       }
/*  74: 75 */       for (int i = 0; i < trainFeatures.length; i++) {
/*  75: 76 */         for (int j = 0; j < featureSize; j++) {
/*  76: 77 */           if (max[j] - min[j] != 0.0D) {
/*  77: 78 */             trainFeatures[i][j] = ((trainFeatures[i][j] - min[j]) / (max[j] - min[j]));
/*  78:    */           }
/*  79:    */         }
/*  80:    */       }
/*  81: 82 */       for (int i = 0; i < testFeatures.length; i++) {
/*  82: 83 */         for (int j = 0; j < featureSize; j++) {
/*  83: 84 */           if (max[j] - min[j] != 0.0D) {
/*  84: 85 */             testFeatures[i][j] = ((testFeatures[i][j] - min[j]) / (max[j] - min[j]));
/*  85:    */           }
/*  86:    */         }
/*  87:    */       }
/*  88: 90 */       for (int i = 0; i < testSamples; i++)
/*  89:    */       {
/*  90: 93 */         double minDist = 1.7976931348623157E+308D;
/*  91: 95 */         for (int j = 0; j < trainSamples; j++)
/*  92:    */         {
/*  93: 96 */           double dist = Distance.chiSquare(testFeatures[i], trainFeatures[j]);
/*  94: 98 */           if (dist < minDist)
/*  95:    */           {
/*  96: 99 */             minDist = dist;
/*  97:100 */             results[i] = trainLabels[j];
/*  98:    */           }
/*  99:    */         }
/* 100:    */       }
/* 101:106 */       double[] accuracies = new double[classCount];
/* 102:108 */       for (int i = 0; i < testLabels.length; i++) {
/* 103:109 */         if (testLabels[i].equalsIgnoreCase(results[i])) {
/* 104:109 */           accuracies[java.lang.Integer.parseInt(testLabels[i])] += 1.0D;
/* 105:    */         }
/* 106:    */       }
/* 107:112 */       for (int i = 0; i < classCount; i++)
/* 108:    */       {
/* 109:113 */         accuracies[i] /= numberOfSamplesPerClass[i];
/* 110:114 */         System.err.println("Accuracy of class " + i + " is " + accuracies[i] * 100.0D + "%");
/* 111:    */       }
/* 112:    */     }
/* 113:    */     catch (Exception e)
/* 114:    */     {
/* 115:121 */       e.printStackTrace();
/* 116:    */     }
/* 117:    */   }
/* 118:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.classifier.NearestNeighbour
 * JD-Core Version:    0.7.0.1
 */