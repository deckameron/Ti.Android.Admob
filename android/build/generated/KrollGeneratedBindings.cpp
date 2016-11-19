/* C++ code produced by gperf version 3.0.3 */
/* Command-line: /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/gperf -L C++ -E -t /private/var/folders/4j/52hn_v_n7ndgkynp8w6hjhwm0000gp/T/douglasalves/admob-generated/KrollGeneratedBindings.gperf  */
/* Computed positions: -k'' */

#line 3 "/private/var/folders/4j/52hn_v_n7ndgkynp8w6hjhwm0000gp/T/douglasalves/admob-generated/KrollGeneratedBindings.gperf"


#include <string.h>
#include <v8.h>
#include <KrollBindings.h>

#include "ti.android.admob.ViewProxy.h"
#include "ti.android.admob.AdmobModule.h"


#line 14 "/private/var/folders/4j/52hn_v_n7ndgkynp8w6hjhwm0000gp/T/douglasalves/admob-generated/KrollGeneratedBindings.gperf"
struct titanium::bindings::BindEntry;
/* maximum key range = 3, duplicates = 0 */

class AdmobBindings
{
private:
  static inline unsigned int hash (const char *str, unsigned int len);
public:
  static struct titanium::bindings::BindEntry *lookupGeneratedInit (const char *str, unsigned int len);
};

inline /*ARGSUSED*/
unsigned int
AdmobBindings::hash (register const char *str, register unsigned int len)
{
  return len;
}

struct titanium::bindings::BindEntry *
AdmobBindings::lookupGeneratedInit (register const char *str, register unsigned int len)
{
  enum
    {
      TOTAL_KEYWORDS = 2,
      MIN_WORD_LENGTH = 26,
      MAX_WORD_LENGTH = 28,
      MIN_HASH_VALUE = 26,
      MAX_HASH_VALUE = 28
    };

  static struct titanium::bindings::BindEntry wordlist[] =
    {
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
#line 16 "/private/var/folders/4j/52hn_v_n7ndgkynp8w6hjhwm0000gp/T/douglasalves/admob-generated/KrollGeneratedBindings.gperf"
      {"ti.android.admob.ViewProxy", ::ti::android::admob::admob::ViewProxy::bindProxy, ::ti::android::admob::admob::ViewProxy::dispose},
      {""},
#line 17 "/private/var/folders/4j/52hn_v_n7ndgkynp8w6hjhwm0000gp/T/douglasalves/admob-generated/KrollGeneratedBindings.gperf"
      {"ti.android.admob.AdmobModule", ::ti::android::admob::AdmobModule::bindProxy, ::ti::android::admob::AdmobModule::dispose}
    };

  if (len <= MAX_WORD_LENGTH && len >= MIN_WORD_LENGTH)
    {
      unsigned int key = hash (str, len);

      if (key <= MAX_HASH_VALUE)
        {
          register const char *s = wordlist[key].name;

          if (*str == *s && !strcmp (str + 1, s + 1))
            return &wordlist[key];
        }
    }
  return 0;
}
#line 18 "/private/var/folders/4j/52hn_v_n7ndgkynp8w6hjhwm0000gp/T/douglasalves/admob-generated/KrollGeneratedBindings.gperf"

