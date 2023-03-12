//
// Created by emoji on 2023/3/3.
//

#include "jna_test.h"
#include <cstdio>
#include "android/log.h"


int SayTest(int i){
  __android_log_print(ANDROID_LOG_DEBUG, "jna_demo","SayTest come from native i=%d", i);
  return i*i;
}
