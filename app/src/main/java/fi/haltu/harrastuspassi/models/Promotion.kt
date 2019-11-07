package fi.haltu.harrastuspassi.models

import java.io.Serializable

class Promotion: Serializable {
    var id: Int = 0
    var title: String = "Promotion otsikko kahdella rivillä"
    var imageUrl: String? = null
    var description: String = "Nullam volutpat tempor metus vel rhoncus. Fusce sodales diam risus, nec hendrerit augue fermentum eu. Donec vitae erat ut libero molestie congue in vitae ligula. Mauris consequat, nibh et consequat vestibulum, dui arcu volutpat purus, ut ultricies diam nunc in ante. Donec quis massa nec erat bibendum semper in nec tortor. Aenean tincidunt elit at blandit laoreet. Mauris tellus nisl, finibus non finibus et, mollis vitae ipsum. Ut odio velit, pulvinar a faucibus in, cursus at purus. Aliquam quis justo quis risus euismod cursus sit amet ac sapien. Cras tristique turpis vel risus ullamcorper, vel pellentesque ipsum malesuada. Praesent sit amet enim non felis ultricies vulputate vitae ullamcorper felis. Vivamus ultricies varius pulvinar. Morbi a felis diam."
    var startDate: String = "2019-12-02"
    var endDate: String = "2019-12-07"
}
