package com.taobao.taobao;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.LibraryResolver;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.ClassLoader;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.wrapper.DvmInteger;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbstractJni {

    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    private MainActivity (){
        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.taobao.taobao").build();
        Memory memory = emulator.getMemory();
        LibraryResolver libraryResolver = new AndroidResolver(23);
        memory.setLibraryResolver(libraryResolver);

        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/example_binaries/taobao/com.taobao.taobao_10.2.5.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/resources/example_binaries/taobao/libsgmainso-6.5.36.so"),false);
        module = dm.getModule();
        dm.callJNI_OnLoad(emulator);
    }

    public void callDoCommandNative(){
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());
        list.add(0);
        DvmInteger dvmInteger = DvmInteger.valueOf(vm,70102);
        StringObject stringObject = new StringObject(vm,"21646297,WGoM/ZYBlZEDAO3E3KWq+3ER&&&21646297&a241b3f8cb26ce65c6fa01e8abcc39b2&1629188669&mtop.taobao.volvo.secondfloor.getconfig&1.0&&231200@taobao_android_10.2.5&AvRGAXrgvGGp-uMBejrmCjkLB5aaTj7sEX7KHLPjkPDi&&&openappkey=DEFAULT_AUTH&27&&&&&&&,false,0,mtop.taobao.volvo.secondfloor.getconfig,pageId=http%3A%2F%2Fm.taobao.com%2Findex.htm&pageName=com.taobao.tao.TBMainActivity,,,,r_10");
        vm.addLocalObject(dvmInteger);
        vm.addLocalObject(stringObject);
        list.add(vm.addLocalObject(new ArrayObject(dvmInteger,stringObject)));
        Number number = module.callFunction(emulator,0x11519,list.toArray())[0];
    }

    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        if ("com/alibaba/wireless/security/mainplugin/SecurityGuardMainPlugin->getMainPluginClassLoader()Ljava/lang/ClassLoader;".equals(signature)) {
            return new ClassLoader(vm, signature);
        }
        return super.callStaticObjectMethod(vm,dvmClass,signature,varArg);
    }

    public static void main(String[] args){
        MainActivity mainActivity = new MainActivity();
        mainActivity.callDoCommandNative();
    }
}