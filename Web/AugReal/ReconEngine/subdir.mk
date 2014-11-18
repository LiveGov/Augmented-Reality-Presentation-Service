################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../Classifier.cpp \
../Files.cpp \
../Main.cpp \
../vlad.cpp 

OBJS += \
./Classifier.o \
./Files.o \
./Main.o \
./vlad.o 

CPP_DEPS += \
./Classifier.d \
./Files.d \
./Main.d \
./vlad.d 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I/home/geol/Downloads/opencv-2.4.4/include/ -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


