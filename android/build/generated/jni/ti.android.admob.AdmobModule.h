/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2011-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

/** This is generated, do not edit by hand. **/

#include <jni.h>

#include "Proxy.h"

namespace ti {
namespace android {
namespace admob {

class AdmobModule : public titanium::Proxy
{
public:
	explicit AdmobModule();

	static void bindProxy(v8::Local<v8::Object>, v8::Local<v8::Context>);
	static v8::Local<v8::FunctionTemplate> getProxyTemplate(v8::Isolate*);
	static void dispose(v8::Isolate*);

	static jclass javaClass;

private:
	static v8::Persistent<v8::FunctionTemplate> proxyTemplate;

	// Methods -----------------------------------------------------------
	static void isLimitAdTrackingEnabled(const v8::FunctionCallbackInfo<v8::Value>&);
	static void resetConsent(const v8::FunctionCallbackInfo<v8::Value>&);
	static void getDebugGeography(const v8::FunctionCallbackInfo<v8::Value>&);
	static void getConsentStatus(const v8::FunctionCallbackInfo<v8::Value>&);
	static void requestConsentInfoUpdateForPublisherIdentifiers(const v8::FunctionCallbackInfo<v8::Value>&);
	static void setTagForUnderAgeOfConsent(const v8::FunctionCallbackInfo<v8::Value>&);
	static void getAdProviders(const v8::FunctionCallbackInfo<v8::Value>&);
	static void setPublisherId(const v8::FunctionCallbackInfo<v8::Value>&);
	static void getAndroidAdId(const v8::FunctionCallbackInfo<v8::Value>&);
	static void setDebugGeography(const v8::FunctionCallbackInfo<v8::Value>&);
	static void isTaggedForUnderAgeOfConsent(const v8::FunctionCallbackInfo<v8::Value>&);
	static void showConsentForm(const v8::FunctionCallbackInfo<v8::Value>&);

	// Dynamic property accessors ----------------------------------------
	static void getter_adProviders(v8::Local<v8::Name> name, const v8::PropertyCallbackInfo<v8::Value>& info);
	static void setter_publisherId(v8::Local<v8::Name> name, v8::Local<v8::Value> value, const v8::PropertyCallbackInfo<void>& info);
	static void setter_tagForUnderAgeOfConsent(v8::Local<v8::Name> name, v8::Local<v8::Value> value, const v8::PropertyCallbackInfo<void>& info);
	static void getter_debugGeography(v8::Local<v8::Name> name, const v8::PropertyCallbackInfo<v8::Value>& info);
	static void setter_debugGeography(v8::Local<v8::Name> name, v8::Local<v8::Value> value, const v8::PropertyCallbackInfo<void>& info);
	static void getter_consentStatus(v8::Local<v8::Name> name, const v8::PropertyCallbackInfo<v8::Value>& info);

};

} // admob
} // android
} // ti
