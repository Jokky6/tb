package com.lazada;

import com.github.unidbg.Module;
import com.github.unidbg.*;
import com.github.unidbg.arm.context.EditableArm32RegisterContext;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.hook.hookzz.HookEntryInfo;
import com.github.unidbg.hook.hookzz.HookZz;
import com.github.unidbg.hook.hookzz.IHookZz;
import com.github.unidbg.hook.hookzz.WrapCallback;
import com.github.unidbg.linux.android.AndroidARM64Emulator;
import com.github.unidbg.linux.android.AndroidARMEmulator;
// import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.ApplicationInfo;
import com.github.unidbg.linux.android.dvm.api.ClassLoader;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.wrapper.DvmBoolean;
import com.github.unidbg.linux.android.dvm.wrapper.DvmInteger;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.linux.file.SimpleFileIO;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.pointer.UnicornPointer;
import com.github.unidbg.utils.Inspector;
import com.sun.jna.Pointer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class lazada extends AbstractJni implements IOResolver {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    public long slot = 0L;

    private static LibraryResolver createLibraryResolver() {
        return new AndroidResolver(23);
    }

    private static AndroidEmulator createARMEmulator() {
        // return AndroidEmulatorBuilder.for32Bit()
        //         .setProcessName("com.lazada.android")
        //         .build();
        return new AndroidARMEmulator("com.lazada.android", new File("target/rootfs"));
    }


    public lazada() {
        emulator = createARMEmulator();
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(createLibraryResolver());
        memory.disableCallInitFunction();
        // vm = emulator.createDalvikVM(new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/base1.apk"));
        vm = emulator.createDalvikVM(new File("E:\\sgmainUnidbg\\unidbg-0.7.0\\unidbg-android\\src\\test\\resources\\example_binaries\\lazada\\com.lazada.android_6.74.0-1312_minAPI19(armeabi-v7a)(nodpi)_apkmirror.com.apk"));
        emulator.getSyscallHandler().addIOResolver(this);
        // emulator.getSyscallHandler().setVerbose(true);
        vm.setVerbose(true);
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary(new File("E:\\sgmainUnidbg\\unidbg-0.7.0\\unidbg-android\\src\\test\\resources\\example_binaries\\lazada\\libsgmainso-6.5.24.so"), true);
        module = dm.getModule();
        // setEnv();c
        hookgetEnv();
        // emulator.attach().addBreakPoint(0x4129e9f5);
        // emulator.attach().addBreakPoint(0x4128158c);
        // emulator.attach().addBreakPoint(0x400a1f97);
        // Logger.getLogger("com.github.unidbg.linux.ARM32SyscallHandler").setLevel(Level.DEBUG);
        // Logger.getLogger("com.github.unidbg.unix.UnixSyscallHandler").setLevel(Level.DEBUG);
        Logger.getLogger("com.github.unidbg.linux.android.ArmLD").setLevel(Level.DEBUG);
        // Logger.getLogger("com.github.unidbg.AbstractEmulator").setLevel(Level.DEBUG);
        Logger.getLogger("com.github.unidbg.linux.android.dvm.DalvikVM").setLevel(Level.DEBUG);
        Logger.getLogger("com.github.unidbg.linux.android.dvm.BaseVM").setLevel(Level.DEBUG);
        // Logger.getLogger("com.github.unidbg.linux.android.dvm").setLevel(Level.DEBUG);


        // DalvikModule dm2 = vm.loadLibrary(new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/libsgsecuritybodyso-6.5.29.so"), true);
        // dm2.callJNI_OnLoad(emulator);

        // DalvikModule dm3 = vm.loadLibrary(new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/libsgmiddletierso-6.5.24.so"), true);
        // dm3.callJNI_OnLoad(emulator);

        dm.callJNI_OnLoad(emulator);

        // Logger.getLogger("com.github.unidbg.linux.ARM32SyscallHandler").setLevel(Level.DEBUG);
        // Logger.getLogger("com.github.unidbg.unix.UnixSyscallHandler").setLevel(Level.DEBUG);
    }

    public void call10101() {
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(10101);

        DvmClass Application = vm.resolveClass("android/app/Application", vm.resolveClass("android/content/ContextWrapper", vm.resolveClass("android/content/Context")));
        DvmClass MyApplication = vm.resolveClass("pt/rocket/app/LazadaApplication", Application);

        DvmObject context = MyApplication.newObject(null);
        vm.addLocalObject(context);

        DvmInteger num = DvmInteger.valueOf(vm, 3);

        StringObject input2 = new StringObject(vm, "");
        vm.addLocalObject(input2);
        StringObject input4 = new StringObject(vm, "");
        vm.addLocalObject(input4);
        StringObject input3 = new StringObject(vm, "/data/user/0/com.lazada.android/app_SGLib");
        vm.addLocalObject(input3);

        list.add(vm.addLocalObject(new ArrayObject(context, num, input2, input3, input4)));
        Number number = module.callFunction(emulator, 0xcc51, list.toArray())[0];
        System.out.println("init1:" + vm.getObject(number.intValue()).getValue());
    }

    public void call10102_1() {
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(10102);

        StringObject input1 = new StringObject(vm, "main");
        vm.addLocalObject(input1);
        StringObject input2 = new StringObject(vm, "6.5.25");
        vm.addLocalObject(input2);
        StringObject input3 = new StringObject(vm, "/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==/lib/arm/libsgmainso-6.5.24.so");
        vm.addLocalObject(input3);

        list.add(vm.addLocalObject(new ArrayObject(input1, input2, input3)));
        Number number = module.callFunction(emulator, 0xcc51, list.toArray())[0];
        System.out.println("init2:" + vm.getObject(number.intValue()).getValue());
    }

    public void call10102_2() {
//         DalvikModule dm2 = vm.loadLibrary(new File("E:\\sgmainUnidbg\\unidbg-0.7.0\\unidbg-android\\src\\test\\resources\\example_binaries\\lazada\\libsgsecuritybodyso-6.5.29.so"), true);
//         dm2.callJNI_OnLoad(emulator);

        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(10102);

        StringObject input1 = new StringObject(vm, "securitybody");
        vm.addLocalObject(input1);
        StringObject input2 = new StringObject(vm, "6.5.29");
        vm.addLocalObject(input2);
        StringObject input3 = new StringObject(vm, "/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==/lib/arm/libsgsecuritybodyso-6.5.29.so");
        vm.addLocalObject(input3);

        list.add(vm.addLocalObject(new ArrayObject(input1, input2, input3)));
        Number number = module.callFunction(emulator, 0xcc51, list.toArray())[0];
        System.out.println("init3:" + vm.getObject(number.intValue()).getValue());
    }

    public void call10102_3() {
         DalvikModule dm3 = vm.loadLibrary(new File("E:\\sgmainUnidbg\\unidbg-0.7.0\\unidbg-android\\src\\test\\resources\\example_binaries\\lazada\\libsgmiddletierso-6.5.24.so"), true);
         dm3.callJNI_OnLoad(emulator);

        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(10102);

//        middletier,6.5.24,/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==/lib/arm/libsgmiddletierso-6.5.24.so
        StringObject input1 = new StringObject(vm, "middletier");
        vm.addLocalObject(input1);
        StringObject input2 = new StringObject(vm, "6.5.24");
        vm.addLocalObject(input2);
        StringObject input3 = new StringObject(vm, "/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==/lib/arm/libsgmiddletierso-6.5.24.so");
        vm.addLocalObject(input3);

        list.add(vm.addLocalObject(new ArrayObject(input1, input2, input3)));
        Number number = module.callFunction(emulator, 0xcc51, list.toArray())[0];
        System.out.println("init3:" + vm.getObject(number.intValue()).getValue());
    }

    public void call70101() {
        // DalvikModule dm3 = vm.loadLibrary(new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/libsgmiddletierso-6.5.24.so"), true);
        // dm3.callJNI_OnLoad(emulator);

        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(70101);

//        middletier,6.5.24,/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==/lib/arm/libsgmiddletierso-6.5.24.so
//         StringObject input1 = new StringObject(vm, "middletier");
//         vm.addLocalObject(input1);
//         StringObject input2 = new StringObject(vm, "6.5.24");
//         vm.addLocalObject(input2);
//         StringObject input3 = new StringObject(vm, "/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==/lib/arm/libsgmiddletierso-6.5.24.so");
//         vm.addLocalObject(input3);

        list.add(null);
        // list.add(null);
        // list.add(null);
        // Number number = module.callFunction(emulator, 0xcc51, list.toArray())[0];
        Number number = module.callFunction(emulator, 0xcc51, list.toArray())[0];
        System.out.println("init3:" + vm.getObject(number.intValue()).getValue());
    }


//    var Intger = Java.use("java.lang.Integer");
//    var jstring = Java.use("java.lang.String");
//    var Boolean = Java.use("java.lang.Boolean");
//    var input0 = jstring.$new("23867946")
//    var input1 = jstring.$new("YG1zUNfVgh8DAFUx0fP7cSZP&&&23867946&88c29b8793d3b1d7500437a8dae998cc&1621807015&mtop.lazada.usergrowth.multiorder.getpoplayerconfigandvoucher&1.0&&600000@lazada_android_6.74.0&&&&&27&&&&&&&")
//    var input2 = Boolean.$new(false);
//    var input3 = Intger.$new(0);
//    var input4 = jstring.$new("mtop.lazada.usergrowth.multiorder.getpoplayerconfigandvoucher")
//    var input5 = jstring.$new("pageId=&pageName=");
//
//    var argList = Java.array("Ljava.lang.Object;", [input0,input1,input2,input3,input4,input5,null,null,null])

    public void callMain() {
        // DalvikModule dm3 = vm.loadLibrary(new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/libsgmainso-6.5.24.so"), true);
        // dm3.callJNI_OnLoad(emulator);

        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        list.add(70102);

        StringObject input0 = new StringObject(vm, "23867946");
        vm.addLocalObject(input0);
        StringObject input1 = new StringObject(vm, "YG1zUNfVgh8DAFUx0fP7cSZP&&&23867946&88c29b8793d3b1d7500437a8dae998cc&1621807015&mtop.lazada.usergrowth.multiorder.getpoplayerconfigandvoucher&1.0&&600000@lazada_android_6.74.0&&&&&27&&&&&&&");
        vm.addLocalObject(input1);

        DvmObject input2 = DvmBoolean.valueOf(vm, false);

        vm.addLocalObject(input2);
        DvmInteger input3 = DvmInteger.valueOf(vm, 0);
        vm.addLocalObject(input3);
        StringObject input4 = new StringObject(vm, "mtop.lazada.usergrowth.multiorder.getpoplayerconfigandvoucher");
        vm.addLocalObject(input4);
        StringObject input5 = new StringObject(vm, "pageId=&pageName=");
        vm.addLocalObject(input5);
        vm.addLocalObject(null);

        StringObject input6 = new StringObject(vm, "");
        vm.addLocalObject(input6);

        StringObject input7 = new StringObject(vm, "");
        vm.addLocalObject(input7);

        StringObject input8 = new StringObject(vm, "");
        vm.addLocalObject(input8);

        list.add(vm.addLocalObject(new ArrayObject(input0, input1, input2, input3, input4, input5, null, null, null)));
        // list.add(vm.addLocalObject(new ArrayObject(input0, input1, input2, input3, input4, input5, input6, input7, input8)));
        // Number number = module.callFunction(emulator, 0xcc51, list.toArray())[0];
        Number number = module.callFunction(emulator, 0xcc51, list.toArray())[0];

        System.out.println("result:" + vm.getObject(number.intValue()).getValue());
    }


    public void setEnv() {
        Symbol setenv = module.findSymbolByName("setenv");
        setenv.call(emulator, "PATH", "/sbin:/system/sbin:/system/bin:/system/xbin:/vendor/bin:/vendor/xbin", 0);
        setenv.call(emulator, "ANDROID_DATA", "/data", 1);
        setenv.call(emulator, "ANDROID_ROOT", "/system", 1);
        // emulator.getMemory()
        // module.callFunction(emulator, 0x3E5A1, )
    }

    ;


    public void hookgetEnv() {
        IHookZz hookZz = HookZz.getInstance(emulator);

        hookZz.wrap(module.findSymbolByName("getenv", true), new WrapCallback<EditableArm32RegisterContext>() {
            String name;

            @Override
            public void preCall(Emulator<?> emulator, EditableArm32RegisterContext ctx, HookEntryInfo info) {
                name = ctx.getPointerArg(0).getString(0);
            }

            @Override
            public void postCall(Emulator<?> emulator, EditableArm32RegisterContext ctx, HookEntryInfo info) {
                switch (name) {
                    case "ANDROID_DATA": {

                        // Pointer result = ctx.getPointerArg(0);
                        // result.setString(0, "/data");

                        MemoryBlock replaceBlock = emulator.getMemory().malloc(0x100, true);
                        UnicornPointer replacePtr = replaceBlock.getPointer();
                        String pathValue = "/data";
                        replacePtr.write(0, pathValue.getBytes(StandardCharsets.UTF_8), 0, pathValue.length());
                        ctx.setR0((replacePtr).toIntPeer());

                        Pointer result = ctx.getPointerArg(0);
                        byte[] resulthex = result.getByteArray(0, 100);
                        // Inspector.inspect(resulthex, "result");
                    }
                    case "ANDROID_ROOT": {
                        // Pointer result = ctx.getPointerArg(0);
                        // result.setString(0, "/system");
                        MemoryBlock replaceBlock = emulator.getMemory().malloc(0x100, true);
                        UnicornPointer replacePtr = replaceBlock.getPointer();
                        String pathValue = "/system";
                        replacePtr.write(0, pathValue.getBytes(StandardCharsets.UTF_8), 0, pathValue.length());
                        ctx.setR0(replacePtr.toIntPeer());
                    }
                }

            }
        });
    }

    ;


    @Override
    public int getStaticIntField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "android/os/Build$VERSION->SDK_INT:I": {
                return 23;
            }
        }
        throw new UnsupportedOperationException(signature);
    }

    public static void main(String[] args) {
//        Logger.getLogger("com.github.unidbg.linux.ARM32SyscallHandler").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.unix.UnixSyscallHandler").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.AbstractEmulator").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.linux.android.dvm.DalvikVM").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.linux.android.dvm.BaseVM").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.linux.android.dvm").setLevel(Level.DEBUG);
        lazada test = new lazada();
//      不知道为啥失效了
//        test.setEnv();
//        test.hookgetEnv();
        test.call10101();

        test.call10102_1();


        test.call10102_2();
        test.call10102_3();


        // test.call70101();

        test.callMain();

    }

    @Override
    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "pt/rocket/app/LazadaApplication->getPackageCodePath()Ljava/lang/String;":
                return new StringObject(vm, "/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==/base.apk");
            case "pt/rocket/app/LazadaApplication->getFilesDir()Ljava/io/File;":
                return vm.resolveClass("java/io/File").newObject(signature);
            case "java/io/File->getAbsolutePath()Ljava/lang/String;":
                // if(dvmObject.getValue().equals("pt/rocket/app/LazadaApplication->getFilesDir()Ljava/io/File;")){
                //     // return new StringObject(vm, "/data/user/0/com.lazada.android/files");
                //     return vm.resolveClass("java/io/File").newObject(signature);
                // }
                return new StringObject(vm, "/data/user/0/com.lazada.android/files");
            case "android/content/Context->getFilesDir()Ljava/io/File;":
                // return vm.resolveClass("java/io/File").newObject("/data/user/0/com.lazada.android/files");
                return vm.resolveClass("java/io/File").newObject(signature);
            case "pt/rocket/app/LazadaApplication->getApplicationInfo()Landroid/content/pm/ApplicationInfo;":
                return new ApplicationInfo(vm);
            // case "android/content/Context->getSystemService(Ljava/lang/String;)Ljava/lang/Object;":
            case "android/content/Context->getSystemService(Ljava/lang/String;)Ljava/lang/Object;":
                String key = (String) varArg.getObject(0).getValue();
                DvmClass object = vm.resolveClass("java/lang/Object");
                DvmClass tele = vm.resolveClass("android/telephony/TelephonyManager", object);
                return tele.newObject(key);

            case "java/util/Enumeration->nextElement()Ljava/lang/Object;":
                return ((Enumeration)dvmObject).nextElement();
            case "java/net/NetworkInterface->getName()Ljava/lang/String;":
                return new StringObject(vm, "wlan0");
            case "android/content/Context->getContentResolver()Landroid/content/ContentResolver;":{
                return vm.resolveClass("android/content/ContentResolver").newObject(signature);
            }
            case "java/lang/Thread->getStackTrace()[Ljava/lang/StackTraceElement;":
                StackTraceElement[] elements = {
                        new StackTraceElement("dalvik.system.VMStack.getThreadStackTrace(Native Method)","Native Method","",0),
                        new StackTraceElement("java.lang.Thread.getStackTrace(Thread.java:1720)","","",0),
                        new StackTraceElement("com.taobao.wireless.security.adapter.JNICLibrary.doCommandNative(Native Method)","","",0),
                        new StackTraceElement("com.alibaba.wireless.security.mainplugin.a.doCommand(Unknown Source:0)","","",0),
                        new StackTraceElement("com.alibaba.wireless.security.middletierplugin.b.d.a.a(Unknown Source:199)","","",0),
                        new StackTraceElement("com.alibaba.wireless.security.middletierplugin.b.d.a$a.invoke(Unknown Source:56)","","",0),
                        new StackTraceElement("java.lang.reflect.Proxy.invoke(Proxy.java:1006)","","",0),
                        new StackTraceElement("$Proxy11.getSecurityFactors(Unknown Source)","","",0),
                        new StackTraceElement("mtopsdk.security.b.a(SourceFile:618)","","",0),
                        new StackTraceElement("mtopsdk.mtop.a.a.a.a.a(SourceFile:207)","","",0),
                        new StackTraceElement("mtopsdk.framework.a.b.d.b(SourceFile:45","","",0),
                        new StackTraceElement("mtopsdk.framework.manager.a.a.a(SourceFile:60)","","",0),
                        new StackTraceElement("mtopsdk.mtop.intf.MtopBuilder.asyncRequest(SourceFile:810)","","",0),
                        new StackTraceElement("mtopsdk.mtop.intf.MtopBuilder.asyncRequest(SourceFile:825)","","",0),
                        new StackTraceElement("com.taobao.tao.remotebusiness.MtopBusiness.startRequest(SourceFile:279)","","",0),
                        new StackTraceElement("com.taobao.tao.remotebusiness.MtopBusiness.startRequest(SourceFile:245)","","",0),
                        new StackTraceElement("com.lazada.android.provider.poplayer.Request.b(SourceFile:152)","","",0),
                        new StackTraceElement("com.lazada.android.provider.poplayer.LazPopLayerDataSource.a(SourceFile:72)","","",0),
                        new StackTraceElement("com.lazada.android.provider.poplayer.LazPopLayerProvider.a(SourceFile:226)","","",0),
                        new StackTraceElement("com.lazada.android.provider.poplayer.LazPopLayerProvider$2.run(SourceFile:327)","","",0),
                        new StackTraceElement("java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)","","",0),
                        new StackTraceElement("java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)","","",0),
                        new StackTraceElement("java.lang.Thread.run(Thread.java:919)","","",0),


                };
                DvmObject[] objs = new DvmObject[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    objs[i] = vm.resolveClass("java/lang/StackTraceElement").newObject(elements[i]);
                }
                return new ArrayObject(objs);
            // case "java/lang/Thread->getStackTrace()[Ljava/lang/StackTraceElement;":
            case "java/lang/StackTraceElement->toString()Ljava/lang/String;":
                StackTraceElement element = (StackTraceElement)dvmObject.getValue();
                return new StringObject(vm, element.getClassName());

            case "dalvik/system/PathClassLoader->findClass(Ljava/lang/String;)Ljava/lang/Class;":
                return vm.resolveClass("java/lang/Class");

            case "java/util/HashMap->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;":
                HashMap value = (HashMap) dvmObject.getValue();
                Object k = varArg.getObject(0).getValue();
                Object v = varArg.getObject(1).getValue();
                Object put = value.put(k, v);
                return vm.resolveClass("java/lang/Object").newObject(put);

        }
        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }



    @Override
    public int callIntMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "android/telephony/TelephonyManager->getSimState()I":
                return 1;
        }
        return super.callIntMethod(vm, dvmObject, signature, varArg);
    }

    @Override
    public DvmObject<?> newObject(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "java/lang/Integer-><init>(I)V":
                System.out.println("test Integer->: " + varArg.getInt(0));
                int i = varArg.getInt(0);
                return DvmInteger.valueOf(vm, i);

            case "com/alibaba/wireless/security/open/SecException-><init>(Ljava/lang/String;I)V":
                StringObject msg = varArg.getObject(0); // 获取第0个参数
                int value = varArg.getInt(1); // 获取第1个参数
                System.out.println("test SecException->" + msg);
                System.out.println("test SecException->" + value);
                return vm.resolveClass("com/alibaba/wireless/security/open/SecException").newObject(null);

            case "java/util/HashMap-><init>(I)V": {
                return vm.resolveClass("java/util/HashMap").newObject(new HashMap<>());
            }
        }

        return super.newObject(vm, dvmClass, signature, varArg);
    }


    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature) {
            case "android/content/pm/ApplicationInfo->nativeLibraryDir:Ljava/lang/String;": {
                return new StringObject(vm, "/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==/lib/arm");
            }
            case "android/content/pm/ApplicationInfo->sourceDir:Ljava/lang/String;":
                return new StringObject(vm, "/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==");
        }
        return super.getObjectField(vm, dvmObject, signature);
    }


    @Override
    public void callStaticVoidMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        System.out.println(signature);
        switch (signature) {
            case "com/alibaba/wireless/security/open/edgecomputing/ECMiscInfo->registerAppLifeCyCleCallBack()V":
                System.out.println("call registerAppLifeCyCleCallBack");
        }
    }

    @Override
    public long getStaticLongField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "com/alibaba/wireless/security/framework/SGPluginExtras->slot:J": {
                System.out.println("get slot");
                return slot;
            }
        }
        return super.getStaticLongField(vm, dvmClass, signature);
    }

    @Override
    public void setStaticLongField(BaseVM vm, DvmClass dvmClass, String signature, long value) {
        switch (signature) {
            case "com/alibaba/wireless/security/framework/SGPluginExtras->slot:J": {
                System.out.println("set slot");
                slot = value;
                return;
            }
        }
        super.setStaticLongField(vm, dvmClass, signature, value);
    }

    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        System.out.println("callStaticObjectMethod:"+signature);
        switch (signature) {
            case "com/taobao/wireless/security/adapter/common/SPUtility2->readFromSPUnified(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;": {
                DvmObject<?> arg0 = varArg.getObject(0);
                DvmObject<?> arg1 = varArg.getObject(1);
                DvmObject<?> arg2 = varArg.getObject(2);
                System.out.println("readFromSPUnified:" + arg0.getValue());
                if (arg0.getValue().equals("Soft")) {
                    return new StringObject(vm, "");
                }
                if (arg0.getValue().equals("local_rw_config")) {
                    return new StringObject(vm, "");
                }
                if (arg0.getValue().equals("llc-local")) {
                    return new StringObject(vm, "1627544452");
                }
                if (arg0.getValue().equals("LOCAL_DEVICE_INFO")) {
                    return new StringObject(vm, "YPUzbAdBVDYDADTQ0dDljsDe");
                }
                System.out.println("fuck:" + signature);
                break;
            }
            case "com/alibaba/wireless/security/securitybody/SecurityGuardSecurityBodyPlugin->getPluginClassLoader()Ljava/lang/ClassLoader;": {
                return new ClassLoader(vm, signature);
            }
            case "com/taobao/dp/util/CallbackHelper->getInstance()Lcom/taobao/dp/util/CallbackHelper;": {
                return vm.resolveClass("com/taobao/dp/util/CallbackHelper").newObject(null);
            }
            case "com/taobao/wireless/security/adapter/datacollection/DeviceInfoCapturer->doCommandForString(I)Ljava/lang/String;": {
                int arg0 = varArg.getInt(0);
                System.out.println("doCommandForString:(" + arg0+")");
                if(arg0 == 122) {
                    return new StringObject(vm, "com.lazada.android");
                }
                if(arg0 == 123) {
                    return new StringObject(vm, "6.74.0");
                }if(arg0 == 135) {
                    return new StringObject(vm, "YPUzbAdBVDYDADTQ0dDljsDe");
                }
                break;
            }
            case "java/net/NetworkInterface->getNetworkInterfaces()Ljava/util/Enumeration;":
                // vm.resolveClass("java/util/Enumeration");
                // return vm.resolveClass("java/util/Enumeration").newObject(new E);
                DvmObject<?> dvmObject = vm.resolveClass("java/net/NetworkInterface").newObject(signature);
                List obj = new ArrayList<DvmObject>();
                obj.add(dvmObject);
                return new Enumeration(vm,  obj);
            case "java/lang/Thread->currentThread()Ljava/lang/Thread;":
                return vm.resolveClass("java/lang/Thread").newObject(signature);

            case "com/alibaba/wireless/security/securitybody/SecurityBodyAdapter->doAdapter(I)Ljava/lang/String;":
                int i = varArg.getInt(0);
                System.out.println("call doAdapter >>" + i);
                if (i==6) {
                    return new StringObject(vm, "100");
                } else if (i == 8) {
                    return new StringObject(vm, "1");
                } else {
                    return null;
                }

        }

        return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);
    }

    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        // System.out.println("！！！！" + pathname);
        switch (pathname) {
            // case "/data/user/0/com.lazada.android/app_SGLib/SG_INNER_DATA":{
            //     return FileResult.success(new SimpleFileIO(oflags, new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/SG_INNER_DATA"), pathname));
            // }
            case "/data/app/com.lazada.android-xhQkwHA2uww872RArng5Rg==/base.apk": {
                // return FileResult.success(new SimpleFileIO(oflags, new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/com.lazada.android_6.74.0-1312_minAPI19(armeabi-v7a)(nodpi)_apkmirror.com.apk"), pathname));
                return FileResult.success(new SimpleFileIO(oflags, new File("E:\\sgmainUnidbg\\unidbg-0.7.0\\unidbg-android\\src\\test\\resources\\example_binaries\\lazada\\base1.apk"), pathname));
            }
            // case "/data/user/0/com.lazada.android/files/storage/com.taobao.maindex": {
            //     return FileResult.success(new ByteArrayFileIO(oflags, pathname, "0".getBytes(StandardCharsets.UTF_8)));
            // }
            // case "/data/user/0/com.lazada.android/files/ab914f43b8296c2c.lock":{
            //     return FileResult.success(new SimpleFileIO(oflags, new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/ab914f43b8296c2c.lock"), pathname));
            // }
            //
            // case "/data/user/0/com.lazada.android/files/0a231bd8575dcf72.txt":{
            //     return FileResult.success(new SimpleFileIO(oflags, new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/0a231bd8575dcf72.txt"), pathname));
            // }
            // case "/user/0/com.lazada.android/files/.ba2f9c85.lock":{
            //     return FileResult.success(new SimpleFileIO(oflags, new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/.ba2f9c85.lockt"), pathname));
            // }
            // case "/data/user/0/com.lazada.android/files/JX0WDG83P1ZN.txt":{
            //     return FileResult.success(new SimpleFileIO(oflags, new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/JX0WDG83P1ZN.txt"), pathname));
            // }
            // case "/data/user/0/com.lazada.android/files/sgFile.lock":{
            //     return FileResult.success(new SimpleFileIO(oflags, new File("/Users/user/Downloads/unidbg-0.7.0/unidbg-android/src/test/resources/example_binaries/lazada/sgFile.lock"), pathname));
            // }

        }
        return null;
    }

    @Override
    public int callStaticIntMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "com/alibaba/wireless/security/framework/utils/UserTrackMethodJniBridge->utAvaiable()I": {
                return 1;
            }
            case "com/uc/crashsdk/JNIBridge->registerInfoCallback(Ljava/lang/String;IJI)I": {
                return 1;
            }
            case "android/provider/Settings$Secure->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I":
                String arg = (String) varArg.getObject(1).getValue();
                System.out.println("查询参数："+arg);
                // return new (vm, "353626076466627");
                if(arg.equals("adb_enabled")){
                    return 0;
                }
                return 0;
        }
        return super.callStaticIntMethod(vm, dvmClass, signature, varArg);
    }

    @Override
    public boolean callBooleanMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "java/util/Enumeration->hasMoreElements()Z":
                return ((Enumeration)dvmObject).hasMoreElements();
            case "java/util/ArrayList->isEmpty()Z":
                return ((ArrayListObject) dvmObject).isEmpty();
            case "java/util/Iterator->hasNext()Z":
                Iterator<?> iterator = (Iterator<?>) dvmObject.getValue();
                return iterator.hasNext();
            case "java/net/NetworkInterface->isUp()Z":
                return true;


        }

        return super.callBooleanMethod(vm, dvmObject, signature, varArg);
    }
}
