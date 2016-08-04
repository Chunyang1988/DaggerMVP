# DaggerMVP[Dagger2](https://github.com/google/dagger)现在由google接管维护，它最主要的好处是通过apt插件在编译阶段时，用来生成注入的代码。
主要作用是为了解决耦合问题，具体讲解可以额看其他文章说的比较多了

我这里就是讲MVP于Dagger结合使用的一个实例，主要是看apt自动生成的文件，方便读者理解的一篇文章，在文章最后会放入代码

#使用

在Project中build.gradle中添加apt支持。


	dependencies {
	    ...
	   classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
	    ...
	}

在Module中build.gradle中添加

	apply plugin: 'com.neenbedankt.android-apt'

	dependencies {
	   compile 'com.google.dagger:dagger:2.0.2'
	   compile 'com.google.dagger:dagger-compiler:2.0.2'
	   compile 'org.glassfish:javax.annotation:10.0-b28'
	}


#基本注释讲解
</br>
##Module
@Module用于类前注释，紧接着会用到@Provides用于当前类中的方法。


	@Module
	public class LoginModule {

	    private LoginControl.LoginView view;

	    public LoginModule(LoginControl.LoginView view) {
	        this.view = view;
	    }

	    @Provides
	    LoginControl.LoginView provideView() {
	        return view;
	    }

	    @Provides
	    LoginFunc provideFunc() {
	        return new LoginFunc();
	    }
	}


来看一下apt生成两个Provides的代码：

LoginFunc方法apt实现：

	public final class LoginModule_ProvideFuncFactory implements Factory<LoginFunc> {
	  private final LoginModule module;

	  public LoginModule_ProvideFuncFactory(LoginModule module) {  
	    assert module != null;
	    this.module = module;
	  }

	  @Override
	  public LoginFunc get() {  
	    LoginFunc provided = module.provideFunc();
	    if (provided == null) {
	      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
	    }
	    return provided;
	  }

	  public static Factory<LoginFunc> create(LoginModule module) {  
	    return new LoginModule_ProvideFuncFactory(module);
	  }
	}



LoginView方法apt实现：

	public final class LoginModule_ProvideViewFactory implements Factory<LoginView> {
	  private final LoginModule module;

	  public LoginModule_ProvideViewFactory(LoginModule module) {  
 	   assert module != null;
 	   this.module = module;
 	 }

 	 @Override
 	 public LoginView get() {  
 	   LoginView provided = module.provideView();
 	   if (provided == null) {
 	     throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
 	   }
 	   return provided;
	  }

	  public static Factory<LoginView> create(LoginModule module) {  
 	   return new LoginModule_ProvideViewFactory(module);
	  }
	}

</br>
##Inject
@Inject依赖支持，用于构造器、方法、作用域


	public class LoginPresenter implements LoginControl.LoginPresenter {


		private LoginControl.LoginView view;
		@Inject
		LoginFunc loginFunc;

		@Inject
		public LoginPresenter(LoginControl.LoginView view) {
			this.view = view;
		}


		@Override
		public void login() {
			view.showProgress();
			loginFunc.onLogin(new Subscriber<Login>() {
				@Override
				public void onCompleted() {

				}

				@Override
				public void onError(Throwable e) {
					view.dismissProgress();
					view.onError(e.getMessage());
				}

				@Override
				public void onNext(Login login) {
					view.dismissProgress();
					view.onResult(login);
				}
			});


		}
	}


在上面中有两处@Inject，需要注意的是在属性中apt生成的类会添加MembersInjector接口，而构造器中则不同，需要添加Factory接口，接着看apt生成文件

###1.构造器上加@Inject

首先会在Module中找构造器中的参数，进行关联，如果没有则会报错，看一下apt生成代码，先说明一下，在生成Inject代码时候，create的时候都会传递一个引用的地方，即@Inject在哪使用的类，在此处（构造器中）当然就是自己的类


	public final class LoginPresenter_Factory implements Factory<LoginPresenter> {
	  private final MembersInjector<LoginPresenter> membersInjector;
	  private final Provider<LoginView> viewProvider;

	  public LoginPresenter_Factory(MembersInjector<LoginPresenter> membersInjector, Provider<LoginView> viewProvider) {  
		assert membersInjector != null;
		this.membersInjector = membersInjector;
		assert viewProvider != null;
		this.viewProvider = viewProvider;
	  }

	  @Override
	  public LoginPresenter get() {  
		LoginPresenter instance = new LoginPresenter(viewProvider.get());
		membersInjector.injectMembers(instance);
		return instance;
	  }

	  public static Factory<LoginPresenter> create(MembersInjector<LoginPresenter> membersInjector, Provider<LoginView> viewProvider) {  
		return new LoginPresenter_Factory(membersInjector, viewProvider);
	  }
	}


###2.属性中加@Inject

 ######  2.1 LoginPresenter中的@Inject

	public final class LoginPresenter_MembersInjector implements MembersInjector<LoginPresenter> {
	  private final Provider<LoginFunc> loginFuncProvider;

	  public LoginPresenter_MembersInjector(Provider<LoginFunc> loginFuncProvider) {  
		assert loginFuncProvider != null;
		this.loginFuncProvider = loginFuncProvider;
	  }

	  @Override
	  public void injectMembers(LoginPresenter instance) {  
		if (instance == null) {
		  throw new NullPointerException("Cannot inject members into a null reference");
		}
		instance.loginFunc = loginFuncProvider.get();
	  }

	  public static MembersInjector<LoginPresenter> create(Provider<LoginFunc> loginFuncProvider) {  
		  return new LoginPresenter_MembersInjector(loginFuncProvider);
	  }
	}


 ###### 2.2 LoginActivity中的@Inject

	public final class LoginActivity_MembersInjector implements MembersInjector<LoginActivity> {
	  private final MembersInjector<AppCompatActivity> supertypeInjector;
	  private final Provider<LoginPresenter> loginPresenterProvider;

	  public LoginActivity_MembersInjector(MembersInjector<AppCompatActivity> supertypeInjector, Provider<LoginPresenter> loginPresenterProvider) {  
		assert supertypeInjector != null;
		this.supertypeInjector = supertypeInjector;
		assert loginPresenterProvider != null;
		this.loginPresenterProvider = loginPresenterProvider;
	  }

	  @Override
	  public void injectMembers(LoginActivity instance) {  
		if (instance == null) {
		  throw new NullPointerException("Cannot inject members into a null reference");
		}
		supertypeInjector.injectMembers(instance);
		instance.loginPresenter = loginPresenterProvider.get();
	  }

	  public static MembersInjector<LoginActivity> create(MembersInjector<AppCompatActivity> supertypeInjector, Provider<LoginPresenter> loginPresenterProvider) {  
		  return new LoginActivity_MembersInjector(supertypeInjector, loginPresenterProvider);
	  }
	}

</br>
###Component
@Component桥梁工具，将Module中的Provides于Inject连接起来


	@Component(modules = LoginModule.class)
	public interface LoginComponent {

		void inject(LoginActivity activity);

	}


Component的apt实现方式如下：


	public final class DaggerLoginComponent implements LoginComponent {
	  private Provider<LoginFunc> provideFuncProvider;
	  private MembersInjector<LoginPresenter> loginPresenterMembersInjector;
	  private Provider<LoginView> provideViewProvider;
	  private Provider<LoginPresenter> loginPresenterProvider;
	  private MembersInjector<LoginActivity> loginActivityMembersInjector;

	  private DaggerLoginComponent(Builder builder) {  
		assert builder != null;
		initialize(builder);
	  }

	  public static Builder builder() {  
		return new Builder();
	  }

	  private void initialize(final Builder builder) {  
		this.provideFuncProvider = LoginModule_ProvideFuncFactory.create(builder.loginModule);
		this.loginPresenterMembersInjector = LoginPresenter_MembersInjector.create(provideFuncProvider);
		this.provideViewProvider = LoginModule_ProvideViewFactory.create(builder.loginModule);
		this.loginPresenterProvider = LoginPresenter_Factory.create(loginPresenterMembersInjector, provideViewProvider);
		this.loginActivityMembersInjector = LoginActivity_MembersInjector.create((MembersInjector) MembersInjectors.noOp(), loginPresenterProvider);
	  }

	  @Override
	  public void inject(LoginActivity activity) {  
		loginActivityMembersInjector.injectMembers(activity);
	  }

	  public static final class Builder {
		private LoginModule loginModule;
  
		private Builder() {  
		}
  
		public LoginComponent build() {  
		  if (loginModule == null) {
			throw new IllegalStateException("loginModule must be set");
		  }
		  return new DaggerLoginComponent(this);
		}
  
		public Builder loginModule(LoginModule loginModule) {  
		  if (loginModule == null) {
			throw new NullPointerException("loginModule");
		  }
		  this.loginModule = loginModule;
		  return this;
		}
	  }
	}


此文都是本人看apt总结写的，有不对的请大家指正。