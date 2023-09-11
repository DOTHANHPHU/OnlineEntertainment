use master
go

create database java4asm
go

use java4asm
go

--TABLE
--có dấu [] bởi vì user là 1 từ khóa của sql nên phải để []
create table [user] 
(
	id              int             primary key identity,
	username        varchar(10)     unique not null,
    [password]      varchar(10)     not null,
    email           varchar(50)     unique not null,
    isAdmin         bit             not null default 0,
    isActive        bit			    not null default 1
)
go

create table video 
(
    id              int             primary key identity,
    title           nvarchar(255)   not null,
    href            varchar(50)     unique not null,
    poster          varchar(255)    null,
    [views]         int             not null default 0,
    shares          int             not null default 0,
    [description]   nvarchar(255)   not null,
    isActive        bit             not null default 1
)
go

create table history
(
    id              int             primary key identity,
    userId          int             foreign key references [user](id),
    videoId         int             foreign key references video(id),
    viewedDate      datetime        not null default getdate(),
    isLiked         bit             not null default 0,
    likedDate        datetime        null
)
go


insert into [user] (username, [password], email, isAdmin) values
('phudt',         '111',      'dothanhphu111@gmail.com',		1),
('phongdt',       '222',      'dothanhphong222@gmail.com',      0),
('nhungoc',       '333',      'nguyennhungoc333@gmail.com',     0),
('thanhphu',      '444',      'dothanhphu01012002@gmail.com',   0)
go

--INSERT INTO----------------------------------------------------------------------------------------------------------------------------------------------------
insert into video (title, href, [description]) values
(N'GIÓ - JANK | OFFICIAL MUSIC VIDEO | VALENTINE 2023',								'igoE062z76U',      'JANK'),
(N'MONO - Em Là (Album 22 - Track No.03)',											'sRUoOA21ZFI',      'Mono Official'),
(N'Lửng Lơ | MASEW x BRAY ft. RedT x Ý Tiên | MV OFFICIAL',							'HehotFZ8BGo',      'Masew'),
(N'CÔ GÁI NÀY LÀ CỦA AI | Krix X Rush ft. Nhi Nhi | OFFICIAL MUSIC VIDEO',			'44c7BxTfL44',      'KxR Official'),
(N'ngủ một mình ft. negav (prod. by kewtiie) | official mv',						'STjzkjnLlZ4',		'HIEUTHUHAI'),
(N'ĐỘ TỘC 2 | FROM MIXI WITH LOVE [OFFICIAL MV LYRIC]',								'Jk38OqdAQxc',      'MASEW x PHÚC DU x PHÁO x ĐỘ MIXI'),
(N'STREAM ĐẾN BAO GIỜ | OFFICIAL MUSIC VIDEO',										'jk7LbXUpmz0',      'ĐỘ MIXI ft. BẠN SÁNG TÁC'),
(N'Bộ Tộc Cùng Già [OFFICIAL MV LYRIC]',											'R8CRG6iFi-Q',      'Thiện Hưng x Entidi x Dũng Đặng')

go

insert into history (userId, videoId, isLiked, likedDate) values
(1, 1, 1, getdate()),
(1, 2, 0, NULL),
(2, 8, 1, getdate()),
(2, 3, 0, NULL),
(3, 8, 1, getdate()),
(3, 7, 0, NULL),
(4, 8, 1, getdate()),
(4, 6, 0, NULL)
go

--QUERY----------------------------------------------------------------------------------------------------------------------------------------------------
--history
select 
	v.id, v.title, v.href, sum(cast(h.isLiked as int)) as totalLike
from 
	video v left join history h on v.id = h.videoId
where
	v.isActive = 1
group by
	v.id, v.title, v.href
order by 
	sum(cast(h.isLiked as int)) desc

--store
create proc sp_selectUsersLikedVideoByVideoHref(@videoHref varchar(50))
as begin
	select 
		u.id, u.username, u.[password], u.email, u.isAdmin, u.isActive
	from 
		video v inner join history h on  v.id = h.videoId
				left join [user] u on u.id = h.userId
	where
		v.href = @videoHref and u.isActive = 1 and v.isActive = 1 and h.isLiked = 1
end