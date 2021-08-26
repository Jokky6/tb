package com.xiayu;

import com.github.unidbg.*;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.hook.HookContext;
import com.github.unidbg.hook.ReplaceCallback;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.wrapper.DvmBoolean;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.hook.hookzz.HookZz;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.linux.android.dvm.wrapper.DvmInteger;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.spi.SyscallHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TianMao1 extends AbstractJni implements IOResolver<AndroidFileIO> {
    private final AndroidEmulator emulator;
    private final VM vm;

    private final HookZz zz;
    private final JSONObject data;
    private Module libc;
    private long slot;

    public String dataAppPath = "/data/app/com.tmall.wireless-NsaOVgz2fomXJNoPTrbOwg==";
    public String packageName = "com.tmall.wireless";
    public String libsgmainso = "unidbg-android/src/test/resources/test_so/tianmao8110/libsgmainso-6.4.156.so";
    public File libsgmain = new File(libsgmainso);
    public String libsgsecuritybodyso = "unidbg-android/src/test/resources/test_so/tianmao8110/libsgsecuritybodyso-6.4.90.so";
    public File libsgsecuritybody = new File(libsgsecuritybodyso);
    public String libsgavmpso = "unidbg-android/src/test/resources/test_so/tianmao8110/libsgavmpso-6.4.34.so";
    public File libsgavmp = new File(libsgavmpso);
    public String libsgmiscso = "unidbg-android/src/test/resources/test_so/tianmao8110/libsgmiscso-6.4.44.so";
    public File libsgmisc = new File(libsgmiscso);

    public String APK_INSTALL_PATH = dataAppPath + "/base.apk";
    public File APK_FILE = new File("/Users/admin/Desktop/android/file/tianmao-8.11.0.apk");

    private static LibraryResolver createLibraryResolver() {
        return new AndroidResolver(23);
    }

    private static AndroidEmulator createARMEmulator() {
        return AndroidEmulatorBuilder.for32Bit().setRootDir(new File("appFile/tianmao")).build();
    }

    public TianMao1() throws JSONException {
        emulator = createARMEmulator();

        Map<String, Integer> iNode = new LinkedHashMap<>();
        iNode.put("/data/system", 671745);
        iNode.put("/data/app", 327681);
        iNode.put("/sdcard/android", 294915);
        iNode.put("/data/user/0/com.tmall.wireless", 655781);
        iNode.put("/data/user/0/com.tmall.wireless/files", 655864);
        emulator.set("inode", iNode);
        emulator.set("uid", 10074);

        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(createLibraryResolver());
        SyscallHandler<AndroidFileIO> handler = emulator.getSyscallHandler();
        handler.setVerbose(false);
        handler.addIOResolver(this);

        vm = emulator.createDalvikVM(APK_FILE);
        vm.setJni(this);
        vm.setVerbose(true);

//        new AndroidModule(emulator, vm).register(memory);

        zz = HookZz.getInstance(emulator);
        data = new JSONObject("{\"Soft_SGSAFETOKEN\":\"8693&snatqgCso35qQYY3F5mQSoIgHiNSSiYMHZGBGtTtLBH57rl\\/\\/ucb40cizd8hypTxpRCA42bf75Nj\\/M3XtApJ9oUjSuY4AK4HR7qCf6UlO\\/\\/G9cv96mz12fV0C8kHhRbuGP8UAuCkVXvf3OiuaAqZqAgWbAfYdBCCfG5staeHvJp2DmUatvRg4O1LsaXO\\/q3sUmJntKBYkTkIbSykADxZBV5tEcR\\/5oyyAF3S2dkeLgmqEKcCu7j0F7qhzcxZfWpuRMaRubEdhiYc4x9nImeEGCvsSpcdYE8LSfJirjA63FPzZ5K0zP9mc6CwX0ISWRCOz\\/Tpjver6QkmiPa2wDePjckwyGhB45JeZtgO0rqCJluOQ3C\\/WSPd3Kjz98g1bVNXxA8WV9qF6A628Xfzdn+aqUYxcJcdG2wG5lIVS6XqiS1P08hC5LCk6Fi0P4vBcfRyFaJ97cUo04GJa\\/DlrcnOVVYF2RKodLm9rFKb\\/X6DiVoO9a28CNR\\/fnsAa0jkxLaLh2pt+tKaEX\\/5Fga2hHREqlTyucUuKVScZmbKb3K9KCOQUFCJm5hj4UYFWiIwG+36AKldAj76tLxwjNsUeQP2eQERibNfFgLnr+Ydm0lkVwhQQYp7phUM8zWEqY98JvUf3h9OoLyPPUG28OBP2RQExkBSmpcOfJYaKM4iIdA1RfzIRU5bhZiyLcWb7jn0rI2j1I5GV2dTmnxz7Bkosfitoc80O5fbQB2QvDwCes0uJBwExip3VjQCp2RTmbrk5d7CKA3fpkw3k4UiRGIyrvO8u88IyHw\\/mt9nbD0RCBDmeEEo4taTUWkb5LLpvzoGBmXzzdcVuCWJJT1Wc\\/MYpIFByQ==\",\"Soft_SGTMAGIC\":\"4I1q9PXiORQGtBivoqf4hSwMk9pwm1D8o4NitR+kvgA=\",\"dynamicreid_dynamicreid\":\"d0666b5b6022eb0\",\"dynamicrsid_dynamicrsid\":\"e1a2607877e260b\",\"SgDyUpdate_ac7123c301ca455b\":\"1621600637\",\"LOCAL_DEVICE_INFO_982c1b269b8e023e5aede2421cbf9c48\":\"YKepcS4SY+ADAIS37Xj5c7s+\",\"DynamicData_accs_ssl_key2_https:\\/\\/ossgw.alicdn.com_21646297%[B\":\"nRWwrMQ\\/jz+oOTWkAZ5FOjhnS1k48SqJdb3w3u\\/ImZJMSXQnlxpD8g0Lyi4kEfgHy5Me33VQ8fyLfqHjPk5PXZ3SwQDtSG4Km7fj9RhEav6NeP85kaWorOA8KTx9u9MHnXdbQa4GVOpBTln\\/GKsPje5gRpmCtWUb71auNwVEO\\/s9LUhH\\/HOcH\\/fwdPixaJAi\\/wNKYYlijdORJgVTOwrtSls1DeUr61NyCDUQa0SkVhw6\\/8PI8gdM1JNt8QEcBIemgI0sM4zA3yyRxFTb0wwcu8CpLsBmIqxqZbvHA+2081dfYDIKuKguH9vYy4s\\/q++odPRvTB25RuEfvXWW\\/+IPtScYQXMx9\\/MG4RW7t80WR0+DOWZXHtkpVlPhTDcU9P2fI4bcQdRSTOIcaI6uFmnOdmb5b9QdtwU3qXgSOuBTh2Bdd6yTeyydRLChBzlWRtcZm6+tYgHOTJIWRNoDg8CxEw==\",\"llc-local_2c3c7f544c159842\":\"1621600921\",\"llc-local_abv2\":\"his:0\",\"llc-local_tcv2\":\"source:0,0,0\"}");

        for (Module each : memory.getLoadedModules()) {
            if ("libc.so".equals(each.getPath())) {
                libc = each;
                break;
            }
        }
    }

    public void run() {
        DvmClass JNICLibrary = vm.resolveClass("com/taobao/wireless/security/adapter/JNICLibrary");
        String methodSign = "doCommandNative(I[Ljava/lang/Object;)Ljava/lang/Object;";

        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);

        DalvikModule dm = vm.loadLibrary(libsgmain, false);

        this.loadLibHook();
//        this.loadTestHook();
//        this.loadTestHook(dm.getModule());

        dm.callJNI_OnLoad(emulator);

        JNICLibrary.callStaticJniMethodObject(
                emulator, methodSign, 10101,
                new ArrayObject(
                        context,
                        DvmInteger.valueOf(vm, 3),
                        new StringObject(vm, ""),
                        new StringObject(vm, "/data/user/0/" + packageName + "/app_SGLib"),
                        new StringObject(vm, "")
                ));

        JNICLibrary.callStaticJniMethodObject(
                emulator, methodSign, 10102,
                new ArrayObject(
                        new StringObject(vm, "main"),
                        new StringObject(vm, "6.5.156"),
                        new StringObject(vm, libsgmain.getAbsolutePath())
                ));

        DalvikModule sgsecuritybodyso = vm.loadLibrary(libsgsecuritybody, false);
        sgsecuritybodyso.callJNI_OnLoad(emulator);

        JNICLibrary.callStaticJniMethodObject(
                emulator, methodSign, 10102,
                new ArrayObject(
                        new StringObject(vm, "securitybody"),
                        new StringObject(vm, "6.4.90"),
                        new StringObject(vm, libsgsecuritybody.getAbsolutePath())
                ));

        DalvikModule sgavmpso = vm.loadLibrary(libsgavmp, true);
        sgavmpso.callJNI_OnLoad(emulator);

        JNICLibrary.callStaticJniMethodObject(
                emulator, methodSign, 10102,
                new ArrayObject(
                        new StringObject(vm, "avmp"),
                        new StringObject(vm, "6.4.34"),
                        new StringObject(vm, libsgavmp.getAbsolutePath())
                ));

        DalvikModule sgmiscso = vm.loadLibrary(libsgmisc, true);
        sgmiscso.callJNI_OnLoad(emulator);

        JNICLibrary.callStaticJniMethodObject(
                emulator, methodSign, 10102,
                new ArrayObject(
                        new StringObject(vm, "misc"),
                        new StringObject(vm, "6.4.44"),
                        new StringObject(vm, libsgmisc.getAbsolutePath())
                ));

        Map<String, String> map = new HashMap<>();
        map.put("INPUT", "&&&23181017&1c9d79ea8dd4bc5665fb7a7727a30366&1629884606&mtop.tmall.inshopsearch.searchitems&1.0&&231200@tmall_android_8.11.0&AnlJxyMxqq1WP7ZOSURHlmKQRVnNJ8Y7la4LNiuKMrpD&&&27&&&&&&&");
        DvmObject<?> ret = JNICLibrary.callStaticJniMethodObject(
                emulator, methodSign, 10401,
                new ArrayObject(
                        vm.resolveClass("java/util/HashMap").newObject(map),
                        new StringObject(vm, "23181017"),
                        DvmInteger.valueOf(vm, 7),
                        null,
                        DvmBoolean.valueOf(vm, true)
                ));

        System.out.println("ret: " + ret.getValue());

        ret = JNICLibrary.callStaticJniMethodObject(
                emulator, methodSign, 20102,
                new ArrayObject(
                        new StringObject(vm, "1629887697"),
                        new StringObject(vm, "23181017"),
                        DvmInteger.valueOf(vm, 8),
                        new StringObject(vm, ""),
                        new StringObject(vm, "pageId=&pageName="),
                        DvmInteger.valueOf(vm, 0)
                ));

        System.out.println("ret: " + ret.getValue());

    }

    public static void main(String[] args) throws IOException, JSONException {
//        Logger.getLogger("com.github.unidbg.linux.ARM32SyscallHandler").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.unix.UnixSyscallHandler").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.AbstractEmulator").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.linux.android.dvm.DalvikVM").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.linux.android.dvm.BaseVM").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.linux.android.dvm").setLevel(Level.DEBUG);

        TianMao1 tm = new TianMao1();

        tm.run();
        tm.destroy();
    }

    private void destroy() throws IOException {
        emulator.close();
        System.out.println("destroy");
    }

    @Override
    public int callIntMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        if ("java/lang/Integer->intValue()I".equals(signature)) {
            return (Integer) dvmObject.getValue();
        } else if ("android/telephony/TelephonyManager->getSimState()I".equals(signature)) {
            return 1;
        }
        return super.callIntMethod(vm, dvmObject, signature, varArg);
    }

    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "com/alibaba/wireless/security/mainplugin/SecurityGuardMainPlugin->getMainPluginClassLoader()Ljava/lang/ClassLoader;": {
                return vm.resolveClass("java/lang/ClassLoader").newObject(null);
            }
            case "com/taobao/wireless/security/adapter/common/SPUtility2->readFromSPUnified(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;": {
                String temp = null;
                String key = varArg.getObjectArg(0).getValue() + "_" + varArg.getObjectArg(1).getValue();

                System.out.println("key: " + key);
                System.out.println("varArg.getObjectArg(2): " + varArg.getObjectArg(2));

                try {
                    temp = data.getString(key);
                } catch (Exception ignored) {
                }
                return temp == null ? varArg.getObjectArg(2) : new StringObject(vm, temp);
            }
            case "com/taobao/wireless/security/adapter/datacollection/DeviceInfoCapturer->doCommandForString(I)Ljava/lang/String;": {
                String temp;
                System.out.println("varArg.getIntArg(0): " + varArg.getIntArg(0));
                switch (varArg.getIntArg(0)) {
                    case 11:
                        temp = "0";
                        break;
                    case 122:
                        temp = "com.tmall.wireless";
                        break;
                    case 114:
                        temp = "1080*2175";
                        break;
                    case 115:
                        temp = "114978435072";
                        break;
                    default:
                        return null;
                }
                return new StringObject(vm, temp);
            }
            case "com/alibaba/wireless/security/securitybody/LifeCycle->getCurrentActivity()Landroid/app/Activity;": {
                return vm.resolveClass("android/app/Activity").newObject(null);
            }
        }

        return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);
    }

    @Override
    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "java/util/HashMap->keySet()Ljava/util/Set;": {
                HashMap map = (HashMap) dvmObject.getValue();
                return vm.resolveClass("java/util/Set").newObject(map.keySet());
            }
            case "java/util/Set->toArray()[Ljava/lang/Object;": {
                Set set = (Set) dvmObject.getValue();
                Object[] array = set.toArray();
                DvmObject[] objects = new DvmObject[array.length];
                for (int i = 0; i < array.length; i++) {
                    if (array[i] instanceof String) {
                        objects[i] = new StringObject(vm, (String) array[i]);
                    } else {
                        throw new IllegalStateException("array=" + array[i]);
                    }
                }
                return new ArrayObject(objects);
            }
            case "java/util/HashMap->get(Ljava/lang/Object;)Ljava/lang/Object;": {
                HashMap map = (HashMap) dvmObject.getValue();
                Object key = varArg.getObjectArg(0).getValue();
                Object obj = map.get(key);
                if (obj instanceof String) {
                    return new StringObject(vm, (String) obj);
                } else {
                    throw new IllegalStateException("array=" + obj);
                }
            }
            case "android/content/Context->getPackageCodePath()Ljava/lang/String;": {
                return new StringObject(vm, APK_INSTALL_PATH);
            }
            case "android/content/Context->getFilesDir()Ljava/io/File;": {
                return vm.resolveClass("java/io/File").newObject(new File("appFile/tianmao/files"));
            }
            case "java/io/File->getAbsolutePath()Ljava/lang/String;": {
                File file = (File) dvmObject.getValue();
                String filePath = file.getAbsolutePath();

                return new StringObject(vm, filePath);
            }
            case "android/app/Activity->getWindow()Landroid/view/Window;": {
                return vm.resolveClass("android/view/Window").newObject(null);
            }
            case "java/lang/Class->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;": {
                System.out.println("getDeclaredMethod: " + varArg.getObjectArg(0));
                System.out.println("getDeclaredMethod: " + varArg.getObjectArg(1));
                return vm.resolveClass("java/lang/reflect/Method").newObject(null);
            }
            case "android/view/Window->getDecorView()Landroid/view/View;": {
                return vm.resolveClass("android/view/View").newObject(null);
            }
            case "java/lang/reflect/Method->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;": {
                System.out.println("Method: " + varArg.getObjectArg(0));
                System.out.println("Method: " + varArg.getObjectArg(1));
                return vm.resolveClass("java/lang/Object").newObject(null);
            }
        }

        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }

    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature) {
            case "android/content/pm/ApplicationInfo->nativeLibraryDir:Ljava/lang/String;":
                return new StringObject(vm, dataAppPath + "/lib/arm");
        }

        return super.getObjectField(vm, dvmObject, signature);
    }

    @Override
    public DvmObject<?> newObject(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "com/alibaba/wireless/security/open/SecException-><init>(Ljava/lang/String;I)V": {
                StringObject msg = varArg.getObjectArg(0);
                int value = varArg.getIntArg(1);

                System.out.println("SecException.msg  : " + msg);
                System.out.println("SecException.value: " + value);

                return dvmClass.newObject(msg.getValue() + "[" + value + "]");
            }
            case "java/lang/Integer-><init>(I)V":
                int value = varArg.getIntArg(0);
                return DvmInteger.valueOf(vm, value);
        }

        return super.newObject(vm, dvmClass, signature, varArg);
    }

    @Override
    public void setStaticLongField(BaseVM vm, DvmClass dvmClass, String signature, long value) {
        System.out.println("setStaticLongField.signature: " + signature);

        if ("com/alibaba/wireless/security/framework/SGPluginExtras->slot:J".equals(signature)) {
            this.slot = value;
        } else {
            super.setStaticLongField(vm, dvmClass, signature, value);
        }
    }

    @Override
    public void callStaticVoidMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "com/alibaba/wireless/security/securitybody/LifeCycle->registerCallBack()V":
            case "com/alibaba/wireless/security/open/edgecomputing/ECMiscInfo->registerAppLifeCyCleCallBack()V":
                return;
        }
        super.callStaticVoidMethod(vm, dvmClass, signature, varArg);
    }

    @Override
    public int callStaticIntMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "com/alibaba/wireless/security/framework/utils/UserTrackMethodJniBridge->utAvaiable()I":
            case "com/uc/crashsdk/JNIBridge->registerInfoCallback(Ljava/lang/String;IJI)I":
                return 1;
        }
        return super.callStaticIntMethod(vm, dvmClass, signature, varArg);
    }

    @Override
    public long getStaticLongField(BaseVM vm, DvmClass dvmClass, String signature) {
        System.out.println("setStaticLongField.signature: " + signature);

        if ("com/alibaba/wireless/security/framework/SGPluginExtras->slot:J".equals(signature)) {
            return slot;
        } else {
            return super.getStaticLongField(vm, dvmClass, signature);
        }
    }

    @Override
    public int getStaticIntField(BaseVM vm, DvmClass dvmClass, String signature) {
        if ("android/content/pm/PackageManager->PERMISSION_GRANTED:I".equals(signature)) {
            return 1;
        }

        return super.getStaticIntField(vm, dvmClass, signature);
    }

    @Override
    public FileResult<AndroidFileIO> resolve(Emulator<AndroidFileIO> emulator, String pathname, int oflags) {
        pathname = pathname.replace("app_1624848789", "app_1627957761");
        System.out.println("resolve.pathname-11111: " + pathname);

        if (("/proc/" + emulator.getPid() + "/stat").equals(pathname)) {
            return FileResult.<AndroidFileIO>success(
                    new ByteArrayFileIO(oflags, pathname, ("8005 (sh) S 648 8005 8005 34817 9875 4210944 1612 14733 0 0 3 34 55 44 20 0 1 0 5698 27979776 729 18446744073709551615 377712734208 377713027280 549192070560 0 0 0 0 3670016 1208083711 1 0 0 17 3 0 0 0 0 0 377713029120 377713038832 378239930368 549192074880 549192074895 549192074895 549192077289 0").getBytes()));
        }
        if (("/proc/" + emulator.getPid() + "/wchan").equals(pathname)) {
            return FileResult.<AndroidFileIO>success(
                    new ByteArrayFileIO(oflags, pathname, ("SyS_rt_sigsuspend").getBytes()));
        }

        switch (pathname) {
            case "/data/app/com.tmall.wireless-NsaOVgz2fomXJNoPTrbOwg==/base.apk":
                return FileResult.success(emulator.getFileSystem().createSimpleFileIO(
                        APK_FILE, oflags, pathname));
            case "/data/user/0/com.tmall.wireless/files/sg_oc.lock":
            case "/data/user/0/com.tmall.wireless/files/ab914f43b8296c2c.lock":
            case "/data/user/0/com.tmall.wireless/files/0a231bd8575dcf72.txt":
            case "/data/user/0/com.tmall.wireless/files/.ba2f9c85.lock":
            case "/data/user/0/com.tmall.wireless/files/JX0WDG83P1ZN.txt":
            case "/data/user/0/com.tmall.wireless/files/sgFile.lock":
            case "/data/user/0/com.tmall.wireless/app_SGLib/SG_INNER_DATA":
                return FileResult.success(emulator.getFileSystem().createSimpleFileIO(
                        new File("appFile/tianmao", pathname), oflags, pathname));
            case "/data/user/0/com.tmall.wireless/app_SGLib/sec":
            case "/data/user/0/com.tmall.wireless/app_SGLib/lvmreport":
            case "/data/data/com.tmall.wireless/app_SGLib/sec":
                return FileResult.success(emulator.getFileSystem().createDirectoryFileIO(
                        new File("appFile/tianmao", pathname), oflags, pathname));
            case "/proc/self/status": {
                return FileResult.<AndroidFileIO>success(new ByteArrayFileIO(oflags, pathname, ("Name:   sh\n" +
                                "Umask:  0022\n" +
                                "State:  S (sleeping)\n" +
                                "Tgid:   " + emulator.getPid() + "\n" +
                                "Ngid:   0\n" +
                                "Pid:    " + emulator.getPid() + "\n" +
                                "PPid:   648\n" +
                                "TracerPid:      0\n" +
                                "Uid:    0       0       0       0\n" +
                                "Gid:    0       0       0       0\n" +
                                "FDSize: 64\n" +
                                "Groups:\n" +
                                "VmPeak:    28224 kB\n" +
                                "VmSize:    27324 kB\n" +
                                "VmLck:         0 kB\n" +
                                "VmPin:         0 kB\n" +
                                "VmHWM:      2920 kB\n" +
                                "VmRSS:      2920 kB\n" +
                                "RssAnon:             552 kB\n" +
                                "RssFile:            2224 kB\n" +
                                "RssShmem:            144 kB\n" +
                                "VmData:     4784 kB\n" +
                                "VmStk:       132 kB\n" +
                                "VmExe:       288 kB\n" +
                                "VmLib:      2080 kB\n" +
                                "VmPTE:        40 kB\n" +
                                "VmPMD:        12 kB\n" +
                                "VmSwap:        0 kB\n" +
                                "Threads:        1\n" +
                                "SigQ:   3/29395\n" +
                                "SigPnd: 0000000000000000\n" +
                                "ShdPnd: 0000000000000000\n" +
                                "SigBlk: 0000000080000000\n" +
                                "SigIgn: 0000000000380000\n" +
                                "SigCgt: 0000000c4801e4ff\n" +
                                "CapInh: 0000000000000000\n" +
                                "CapPrm: 0000003fffffffff\n" +
                                "CapEff: 0000003fffffffff\n" +
                                "CapBnd: 0000003fffffffff\n" +
                                "CapAmb: 0000000000000000\n" +
                                "NoNewPrivs:     0\n" +
                                "Seccomp:        0\n" +
                                "Speculation_Store_Bypass:       unknown\n" +
                                "Cpus_allowed:   ff\n" +
                                "Cpus_allowed_list:      0-7\n" +
                                "Mems_allowed:   1\n" +
                                "Mems_allowed_list:      0\n" +
                                "voluntary_ctxt_switches:        71\n" +
                                "nonvoluntary_ctxt_switches:     11").getBytes()
                        )
                );
            }
            case "/proc/cpuinfo": {
                return FileResult.<AndroidFileIO>success(new ByteArrayFileIO(oflags, pathname, ("Processor       : AArch64 Processor rev 14 (aarch64)\n" +
                        "processor       : 0\n" +
                        "BogoMIPS        : 38.40\n" +
                        "Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp cpuid as\n" +
                        "imdrdm lrcpc dcpop asimddp\n" +
                        "CPU implementer : 0x51\n" +
                        "CPU architecture: 8\n" +
                        "CPU variant     : 0xd\n" +
                        "CPU part        : 0x805\n" +
                        "CPU revision    : 14\n" +
                        "\n" +
                        "processor       : 1\n" +
                        "BogoMIPS        : 38.40\n" +
                        "Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp cpuid as\n" +
                        "imdrdm lrcpc dcpop asimddp\n" +
                        "CPU implementer : 0x51\n" +
                        "CPU architecture: 8\n" +
                        "CPU variant     : 0xd\n" +
                        "CPU part        : 0x805\n" +
                        "CPU revision    : 14\n" +
                        "\n" +
                        "processor       : 2\n" +
                        "BogoMIPS        : 38.40\n" +
                        "Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp cpuid asimdrdm lrcpc dcpop asimddp\n" +
                        "CPU implementer : 0x51\n" +
                        "CPU architecture: 8\n" +
                        "CPU variant     : 0xd\n" +
                        "CPU part        : 0x805\n" +
                        "CPU revision    : 14\n" +
                        "\n" +
                        "processor       : 3\n" +
                        "BogoMIPS        : 38.40\n" +
                        "Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp cpuid asimdrdm lrcpc dcpop asimddp\n" +
                        "CPU implementer : 0x51\n" +
                        "CPU architecture: 8\n" +
                        "CPU variant     : 0xd\n" +
                        "CPU part        : 0x805\n" +
                        "CPU revision    : 14\n" +
                        "\n" +
                        "processor       : 4\n" +
                        "BogoMIPS        : 38.40\n" +
                        "Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp cpuid asimdrdm lrcpc dcpop asimddp\n" +
                        "CPU implementer : 0x51\n" +
                        "CPU architecture: 8\n" +
                        "CPU variant     : 0xd\n" +
                        "CPU part        : 0x805\n" +
                        "CPU revision    : 14\n" +
                        "\n" +
                        "processor       : 5\n" +
                        "BogoMIPS        : 38.40\n" +
                        "Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp cpuid asimdrdm lrcpc dcpop asimddp\n" +
                        "CPU implementer : 0x51\n" +
                        "CPU architecture: 8\n" +
                        "CPU variant     : 0xd\n" +
                        "CPU part        : 0x805\n" +
                        "CPU revision    : 14\n" +
                        "\n" +
                        "processor       : 6\n" +
                        "BogoMIPS        : 38.40\n" +
                        "Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp cpuid asimdrdm lrcpc dcpop asimddp\n" +
                        "CPU implementer : 0x51\n" +
                        "CPU architecture: 8\n" +
                        "CPU variant     : 0xd\n" +
                        "CPU part        : 0x804\n" +
                        "CPU revision    : 14\n" +
                        "\n" +
                        "processor       : 7\n" +
                        "BogoMIPS        : 38.40\n" +
                        "Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp cpuid asimdrdm lrcpc dcpop asimddp\n" +
                        "CPU implementer : 0x51\n" +
                        "CPU architecture: 8\n" +
                        "CPU variant     : 0xd\n" +
                        "CPU part        : 0x804\n" +
                        "CPU revision    : 14\n" +
                        "\n" +
                        "Hardware        : Qualcomm Technologies, Inc SM7150").getBytes()));
            }
//            case "/system/lib/libdl.so": {
//                String soFile = "unidbg-android/src/test/resources/test_so/libdl.so";
//                return FileResult.success(emulator.getFileSystem().createSimpleFileIO(
//                        new File(soFile), oflags, pathname));
//            }
//            case "/system/bin/vold": {
//                return FileResult.success(emulator.getFileSystem().createSimpleFileIO(
//                        new File("appFile/system_bin_vold"), oflags, pathname));
//            }
//            case "/system/bin/debuggerd": {
//                return FileResult.success(emulator.getFileSystem().createSimpleFileIO(
//                        new File("appFile/system_bin_debuggerd"), oflags, pathname));
//            }

            default:
                return null;
        }
    }

    private void loadTestHook() {
//        zz.replace(libc.findSymbolByName("time"), new ReplaceCallback() {
//            @Override
//            public HookStatus onCall(Emulator<?> emulator, long originFunction) {
//                return HookStatus.LR(emulator, 1618558999);
//            }
//        });
//        zz.replace(libc.findSymbolByName("lrand48"), new ReplaceCallback() {
//            @Override
//            public HookStatus onCall(Emulator<?> emulator, long originFunction) {
//                return HookStatus.LR(emulator, 0);
//            }
//        });
//        zz.replace(libc.findSymbolByName("arc4random"), new ReplaceCallback() {
//            @Override
//            public HookStatus onCall(Emulator<?> emulator, long originFunction) {
//                return HookStatus.LR(emulator, 0x71BDF95F);
//            }
//        });
        zz.replace(libc.findSymbolByName("gettimeofday"), new ReplaceCallback() {
            @Override
            public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
                UnidbgPointer pointerArg = context.getPointerArg(0);
                pointerArg.write(0, new int[]{1629887946, 541000}, 0, 2);
                return HookStatus.LR(emulator, 0);
            }
        });
    }

    private void loadTestHook(Module main) {
        zz.replace(main.base + 0x94584 | 1, new ReplaceCallback() {
            @Override
            public HookStatus onCall(Emulator<?> emulator, long originFunction) {
                return HookStatus.LR(emulator, 0x98764321);
            }
        });
    }

    private void loadLibHook() {
//        zz.replace(libc.findSymbolByName("pthread_create"), new ReplaceCallback() {
//            @Override
//            public HookStatus onCall(Emulator<?> emulator, long originFunction) {
//                return HookStatus.LR(emulator, 0);
//            }
//        });
//
//        zz.replace(libc.findSymbolByName("getuid"), new ReplaceCallback() {
//            @Override
//            public HookStatus onCall(Emulator<?> emulator, long originFunction) {
//                return HookStatus.LR(emulator, emulator.<Integer>get("uid"));
//            }
//        });

        zz.replace(libc.findSymbolByName("stat64"), new ReplaceCallback() {
            private String path;
            private UnidbgPointer buff;

            @Override
            public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
                path = context.getPointerArg(0).getString(0);
                buff = context.getPointerArg(1);
                return super.onCall(emulator, context, originFunction);
            }

            @Override
            public void postCall(Emulator<?> emulator, HookContext context) {
                Object inode = emulator.get("inode");
                if (inode != null) {
                    Object integer = ((Map<?, ?>) inode).get(this.path);
                    if (integer != null) {
                        int[] ints = {(int) integer};
                        buff.write(12, ints, 0, 1);
                        buff.write(0x60, ints, 0, 1);
                    }
                }
                super.postCall(emulator, context);
            }
        }, true);
    }
}
