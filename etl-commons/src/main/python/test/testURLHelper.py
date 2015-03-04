import unittest
from utils import URLHelper


class TestURLHelper(unittest.TestCase):
    def test_decoder(self):
        url = "http://www.amazon.cn/b/ref=dri_3_2134731051?fst=as%3Aoff&rh=n%3A2127215051%2Cn%3A%212127216051%2Cn%3A2134729051%2Cn%3A2134731051&bbn=2134729051&ie=UTF8&qid=1419496127&rnid=2134729051&node=2134731051"
        url = "http%3a%2f%2fs%2em%2etaobao%2ecom%2fsearch%2ehtm%3fspm%3d0%2e0%2e0%2e0%26q%3d%25E6%2599%25AE%25E6%25B4%25B1%2b%2b%26iwRet%3dtrue&amp;category=&amp;userid=&amp;b2c_orid=&amp;b2c_auction=&amp;at_isb=&amp;atp_isdpp=&amp;at_ssid=&amp;bbid=&amp;aplus&amp;at_cart=&amp;at_udid=&&asid=AACHr6hUobHJq7gSq2I="
        url = "http://s.m.taobao.com/search?&q=%E6%9E%9C%E7%8F%8D+&iwRet=true&spm=41.139785.167731.22&sst=1&n=20&buying=buyitnow&m=api4h5&abtest=4&wlsort=4&style=list&closeModues=nav%2Cselecthot%2Conesearch&page=2&callback=jsonp142078392776571608";
        url = "http://tracker.yhd.com/ad-dolphin-go/go?v=_3AZXoii8aM75zbZIqCeWxB5Yx-Ev-75_CVPLmxSE6VFa6kZBPcEisRe3z86fibxEdmt4o2dgee48olFKoHv_4_77_bLUxVg_iVvuU_axWOW0WyfnbBvpCO2_Cu8zuDMqzTsrznehb_wx5Fm0MFpo3GiJMrzLnwsohLH-JYif5o0TTdcw5wUUw1WZ_v9edZ47yxdzq99Zg2TMG3gK4iiOYtQm0sYpSRfCYAmXDF7t6knk1tbdmSjmOoj9n3Mk1ALnVPG70aopZFyH3DKbmYysQ.."

        url = "http://api.m.taobao.com/rest/api3.do?sign=7035593fb529206dc710e7ae4c2597da&ttid=700753%40tmall_android_2.1.1&v=3.1&t=1421759376&imei=865488020658465&data=%7B%22itemNumId%22%3A%2240527285607%22%2C%22sid%22%3A%221cba577057078da4bbd5c6f0fea31099%22%7D&api=mtop.wdetail.getItemDetail&imsi=460002780552151&deviceId=Au_yUUAS4Y-TYaIuSv6rN0ydGrDM8Ad2h7lyz6JYJdm7&appKey=12679450"
        my_url = URLHelper.decoder(url)
        print "src:", url
        print "dst:", my_url
        my_url = URLHelper.decoder(my_url)
        print "dst2:", my_url


if __name__ == '__main__':
    unittest.main()